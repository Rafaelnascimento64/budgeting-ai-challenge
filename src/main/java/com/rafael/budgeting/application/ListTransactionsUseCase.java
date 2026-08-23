package com.rafael.budgeting.application;

import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionRepository;
import com.rafael.budgeting.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> execute(String category, TransactionType type) {
        if (category != null && !category.isBlank()) {
            return repository.findByCategory(category.trim().toUpperCase());
        }
        if (type != null) {
            return repository.findByType(type);
        }
        return repository.findAll();
    }
}
