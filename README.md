# Budgeting AI — Desafio Spring Boot + Spring AI (DIO)

Projeto feito por mim, Rafael Nascimento, como desafio final do bootcamp
**Santander 2026 - Back-end com Java e IA** (DIO). É a evolução do projeto
base do módulo **05-spring-ai** da trilha
[dio-spring-boot-learning-track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track),
mantendo a mesma arquitetura em camadas (domain / application / infrastructure)
usada no restante da trilha.

- GitHub: [github.com/Rafaelnascimento64](https://github.com/Rafaelnascimento64)
- Portfólio: [portifolio-link-six.vercel.app](https://portifolio-link-six.vercel.app)

## O que o projeto faz

Uma API de orçamento pessoal que processa comandos de voz (ou texto) para
registrar e consultar transações financeiras:

1. O cliente envia um áudio (ou texto) com um comando, ex: *"gastei 50 reais
   com mercado"* ou *"qual meu saldo por categoria?"*;
2. O áudio é transcrito para texto (Speech-to-Text via Whisper);
3. O texto é interpretado por um `ChatClient` (Spring AI), que decide qual
   ferramenta (`@Tool`) chamar: registrar transação, listar transações ou
   consultar saldo;
4. A ferramenta executa um **use case real** da aplicação, que persiste ou
   consulta dados no banco;
5. A IA gera uma resposta final em texto para o usuário.

## Melhoria implementada

Entre as sugestões do desafio, foram implementadas duas relacionadas:

- **Novo tipo de consulta financeira**: `GetBalanceByCategoryUseCase`,
  que agrupa todas as transações por categoria e calcula receitas, despesas
  e saldo de cada uma — tanto para todas as categorias quanto para uma
  categoria específica.
- **Validações antes de salvar uma transação**: `CreateTransactionUseCase`
  valida descrição, valor (deve ser positivo) e tipo antes de persistir,
  lançando `IllegalArgumentException` (tratada com `@ExceptionHandler`
  retornando HTTP 400).

Essa nova consulta foi exposta de duas formas:
- Como ferramenta de IA (`consultarSaldoPorCategoria`, em `FinanceTools`),
  usada automaticamente quando o usuário pergunta sobre saldo por voz/texto;
- Como endpoints REST tradicionais (`GET /api/transactions/balance-by-category`
  e `GET /api/transactions/balance-by-category/{category}`).

## Tecnologias usadas

- Java 17
- Spring Boot 3.3
- Spring AI (`spring-ai-openai-spring-boot-starter`) — `ChatClient`, Tool
  Calling, `OpenAiAudioTranscriptionModel` (Whisper)
- Spring Data JPA + H2 (banco em memória, fácil de trocar por Postgres/MySQL)
- Bean Validation (`jakarta.validation`)
- JUnit 5

## Estrutura do projeto

```
src/main/java/com/rafael/budgeting/
├── domain/                     # Entidade e contrato de repositório (sem dependência de framework)
├── application/                # Use cases: criar, listar, saldo por categoria
└── infrastructure/
    ├── persistence/            # Adaptador JPA que implementa o repositório do domínio
    ├── ai/                     # FinanceTools (@Tool) e configuração do ChatClient
    └── web/                    # Controllers REST (transações e assistente de voz)
```

## Como executar

1. Defina sua chave da OpenAI:
   ```
   export OPENAI_API_KEY="sua_chave_aqui"
   ```
2. Rode a aplicação:
   ```
   ./gradlew bootRun
   ```
   > Se o wrapper (`gradlew`) não estiver presente no seu ambiente, gere-o com
   > `gradle wrapper` (usando um Gradle instalado localmente) ou abra o
   > projeto direto na IDE (IntelliJ configura o Gradle automaticamente).
3. Rode os testes:
   ```
   ./gradlew test
   ```

## Como testar o fluxo principal

**Via texto (mais rápido, não precisa gravar áudio):**
```
curl -X POST "http://localhost:8080/api/assistant/text-command" \
  -d "message=gastei 50 reais com mercado na categoria alimentacao"
```

**Via áudio:**
```
curl -X POST "http://localhost:8080/api/assistant/voice-command" \
  -F "audio=@comando.mp3"
```

**Consultar saldo por categoria diretamente via REST:**
```
curl http://localhost:8080/api/transactions/balance-by-category
```

**Criar transação via REST (sem IA):**
```
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"description":"Salário","amount":3000,"type":"RECEITA","category":"salario"}'
```

Console do H2 disponível em `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:budgeting`).

## O que eu aprendi

- Como conectar um `ChatClient` do Spring AI a ferramentas (`@Tool`) que
  chamam use cases reais da aplicação, em vez de deixar a IA manipular dados
  diretamente — mantendo a arquitetura em camadas que a gente já vinha usando
  na trilha.
- Como transcrever áudio com `OpenAiAudioTranscriptionModel` e passar o
  texto resultante de volta pro `ChatClient`.
- Como expor a mesma regra de negócio (saldo por categoria) tanto pra IA
  quanto pra consumidores REST tradicionais, reaproveitando o mesmo use case
  em vez de duplicar lógica.
- A importância de validar os dados antes de persistir, ainda mais quando a
  entrada vem de uma IA interpretando linguagem natural (nem sempre o valor
  ou a categoria vêm exatamente como eu esperava).
- Nunca deixar chaves de API em texto no repositório — usei variável de
  ambiente (`OPENAI_API_KEY`) desde o início por esse motivo.

## Próximos passos (não implementados, mas mapeados)

- Adicionar `TextToSpeechModel` para responder também em áudio.
- Persistir o histórico de conversas por usuário (multi-tenant).
- Cobrir `CreateTransactionUseCase` e o controller REST com testes de
  integração usando `@SpringBootTest` + `MockMvc`.
