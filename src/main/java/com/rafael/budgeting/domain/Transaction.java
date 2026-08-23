package com.rafael.budgeting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Core domain entity. Deliberately framework-agnostic (no JPA annotations here) -
 * persistence details live in the infrastructure layer, following the same
 * layered/DDD approach used across the DIO Spring Boot learning track.
 */
public record Transaction(
        UUID id,
        String description,
        BigDecimal amount,
        TransactionType type,
        String category,
        LocalDate date
) {
    public static Transaction create(String description, BigDecimal amount, TransactionType type,
                                      String category, LocalDate date) {
        return new Transaction(UUID.randomUUID(), description, amount, type, category,
                date != null ? date : LocalDate.now());
    }
}
