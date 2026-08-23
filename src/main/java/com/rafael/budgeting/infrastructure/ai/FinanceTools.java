package com.rafael.budgeting.infrastructure.ai;

import com.rafael.budgeting.application.CreateTransactionUseCase;
import com.rafael.budgeting.application.GetBalanceByCategoryUseCase;
import com.rafael.budgeting.application.ListTransactionsUseCase;
import com.rafael.budgeting.application.dto.CategoryBalance;
import com.rafael.budgeting.application.dto.CreateTransactionInput;
import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionType;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class FinanceTools {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetBalanceByCategoryUseCase getBalanceByCategoryUseCase;

    public FinanceTools(CreateTransactionUseCase createTransactionUseCase,
                         ListTransactionsUseCase listTransactionsUseCase,
                         GetBalanceByCategoryUseCase getBalanceByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getBalanceByCategoryUseCase = getBalanceByCategoryUseCase;
    }

        public String registrarTransacao(String descricao, BigDecimal valor, TransactionType tipo, String categoria) {
        Transaction transaction = createTransactionUseCase.execute(
                new CreateTransactionInput(descricao, valor, tipo, categoria, LocalDate.now())
        );
        return "Transação registrada: %s de %s em %s (categoria: %s)".formatted(
                transaction.type(), transaction.amount(), transaction.description(), transaction.category());
    }

        public String listarTransacoes(String categoria) {
        List<Transaction> transactions = listTransactionsUseCase.execute(categoria, null);
        if (transactions.isEmpty()) {
            return "Nenhuma transação encontrada.";
        }
        StringBuilder sb = new StringBuilder("Transações encontradas:\n");
        for (Transaction t : transactions) {
            sb.append("- %s: %s (%s) em %s\n".formatted(t.type(), t.amount(), t.category(), t.description()));
        }
        return sb.toString();
    }

        public String consultarSaldoPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            List<CategoryBalance> balances = getBalanceByCategoryUseCase.execute();
            if (balances.isEmpty()) {
                return "Nenhuma transação registrada ainda.";
            }
            StringBuilder sb = new StringBuilder("Saldo por categoria:\n");
            for (CategoryBalance b : balances) {
                sb.append("- %s: receitas %s, despesas %s, saldo %s (%d transações)\n".formatted(
                        b.category(), b.totalReceitas(), b.totalDespesas(), b.saldo(), b.quantidadeTransacoes()));
            }
            return sb.toString();
        }

        CategoryBalance balance = getBalanceByCategoryUseCase.executeForCategory(categoria);
        return "Categoria %s: receitas %s, despesas %s, saldo %s (%d transações)".formatted(
                balance.category(), balance.totalReceitas(), balance.totalDespesas(),
                balance.saldo(), balance.quantidadeTransacoes());
    }

            public FunctionCallback[] functionCallbacks() {
            FunctionCallback registrar = FunctionCallbackWrapper
                .<RegistrarTransacaoInput, String>builder(input -> registrarTransacao(
                    input.descricao(), input.valor(), input.tipo(), input.categoria()))
                .withName("registrarTransacao")
                .withDescription("Registra uma nova transação financeira informada pelo usuário.")
                .withInputType(RegistrarTransacaoInput.class)
                .build();

            FunctionCallback listar = FunctionCallbackWrapper
                .<ListarTransacoesInput, String>builder(input -> listarTransacoes(input.categoria()))
                .withName("listarTransacoes")
                .withDescription("Lista as transações financeiras, podendo filtrar por categoria.")
                .withInputType(ListarTransacoesInput.class)
                .build();

            FunctionCallback saldo = FunctionCallbackWrapper
                .<SaldoPorCategoriaInput, String>builder(input -> consultarSaldoPorCategoria(input.categoria()))
                .withName("consultarSaldoPorCategoria")
                .withDescription("Consulta o saldo agrupado por categoria.")
                .withInputType(SaldoPorCategoriaInput.class)
                .build();

            return new FunctionCallback[]{registrar, listar, saldo};
            }

            public record RegistrarTransacaoInput(String descricao, BigDecimal valor, TransactionType tipo,
                              String categoria) {
            }

            public record ListarTransacoesInput(String categoria) {
            }

            public record SaldoPorCategoriaInput(String categoria) {
            }
}
