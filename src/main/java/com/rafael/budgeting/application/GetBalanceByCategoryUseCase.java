package com.rafael.budgeting.application;

import com.rafael.budgeting.application.dto.CategoryBalance;
import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionRepository;
import com.rafael.budgeting.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MELHORIA IMPLEMENTADA NESTE DESAFIO:
 * Novo tipo de consulta financeira. Em vez de apenas listar transações ou
 * calcular um saldo único, esse use case agrupa todas as transações por
 * categoria e devolve receitas, despesas e saldo de cada uma.
 *
 * Isso permite ao usuário perguntar, por comando de voz, algo como:
 * "Qual o meu saldo por categoria?" ou "Como estão meus gastos com mercado?"
 * e a IA usa esta ferramenta (Tool Calling) para responder com dados reais.
 */
@Component
public class GetBalanceByCategoryUseCase {

    private final TransactionRepository repository;

    public GetBalanceByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<CategoryBalance> execute() {
        List<Transaction> transactions = repository.findAll();

        Map<String, List<Transaction>> byCategory = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::category));

        return byCategory.entrySet().stream()
                .map(entry -> toCategoryBalance(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CategoryBalance::category))
                .toList();
    }

    public CategoryBalance executeForCategory(String category) {
        String normalized = category.trim().toUpperCase();
        List<Transaction> transactions = repository.findByCategory(normalized);
        return toCategoryBalance(normalized, transactions);
    }

    private CategoryBalance toCategoryBalance(String category, List<Transaction> transactions) {
        BigDecimal receitas = sumByType(transactions, TransactionType.RECEITA);
        BigDecimal despesas = sumByType(transactions, TransactionType.DESPESA);

        return new CategoryBalance(
                category,
                receitas,
                despesas,
                receitas.subtract(despesas),
                transactions.size()
        );
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.type() == type)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
