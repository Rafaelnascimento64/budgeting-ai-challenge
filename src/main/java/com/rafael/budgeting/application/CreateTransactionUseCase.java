package com.rafael.budgeting.application;

import com.rafael.budgeting.application.dto.CreateTransactionInput;
import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(CreateTransactionInput input) {
        validate(input);

        Transaction transaction = Transaction.create(
                input.description(),
                input.amount(),
                input.type(),
                normalizeCategory(input.category()),
                input.date()
        );

        return repository.save(transaction);
    }

    private void validate(CreateTransactionInput input) {
        if (input.description() == null || input.description().isBlank()) {
            throw new IllegalArgumentException("A descrição da transação é obrigatória.");
        }
        if (input.amount() == null || input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        if (input.type() == null) {
            throw new IllegalArgumentException("O tipo da transação (RECEITA ou DESPESA) é obrigatório.");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "GERAL";
        }
        return category.trim().toUpperCase();
    }
}
