package com.rafael.budgeting.infrastructure.persistence;

import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionRepository;
import com.rafael.budgeting.domain.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    public TransactionRepositoryImpl(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity saved = jpaRepository.save(TransactionEntity.fromDomain(transaction));
        return saved.toDomain();
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll().stream().map(TransactionEntity::toDomain).toList();
    }

    @Override
    public List<Transaction> findByCategory(String category) {
        return jpaRepository.findByCategory(category).stream().map(TransactionEntity::toDomain).toList();
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return jpaRepository.findByType(type).stream().map(TransactionEntity::toDomain).toList();
    }
}
