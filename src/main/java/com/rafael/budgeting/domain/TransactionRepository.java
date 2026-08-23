package com.rafael.budgeting.domain;

import java.util.List;
import java.util.UUID;

/**
 * Repository port (Repository Pattern). Implemented by the infrastructure layer
 * (JPA adapter) and consumed only by the application layer's use cases.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findAll();

    List<Transaction> findByCategory(String category);

    List<Transaction> findByType(TransactionType type);
}
