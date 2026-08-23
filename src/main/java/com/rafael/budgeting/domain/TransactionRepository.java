package com.rafael.budgeting.domain;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findAll();

    List<Transaction> findByCategory(String category);

    List<Transaction> findByType(TransactionType type);
}
