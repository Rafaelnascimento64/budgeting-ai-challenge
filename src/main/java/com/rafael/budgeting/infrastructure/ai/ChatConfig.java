package com.rafael.budgeting.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    private static final String SYSTEM_PROMPT = """
            Você é um assistente financeiro por voz. Sua função é entender comandos
            em português sobre transações financeiras (registrar receitas/despesas,
            listar transações, consultar saldo por categoria) e executar a ferramenta
            correta para cada pedido. Sempre responda de forma curta e direta,
            confirmando o que foi feito ou trazendo os dados solicitados.
            """;

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, FinanceTools financeTools) {
        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultFunctions(financeTools.functionCallbacks())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(new InMemoryChatMemory()).build())
                .build();
    }
}
