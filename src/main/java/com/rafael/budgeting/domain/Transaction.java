package com.rafael.budgeting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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
