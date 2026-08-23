package com.rafael.budgeting.application.dto;

import com.rafael.budgeting.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionInput(
        String description,
        BigDecimal amount,
        TransactionType type,
        String category,
        LocalDate date
) {
}
