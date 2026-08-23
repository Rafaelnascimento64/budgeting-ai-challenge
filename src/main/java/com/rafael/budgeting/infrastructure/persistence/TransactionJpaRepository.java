package com.rafael.budgeting.infrastructure.persistence;

import com.rafael.budgeting.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByCategory(String category);

    List<TransactionEntity> findByType(TransactionType type);
}
