package com.rafael.budgeting.infrastructure.web.dto;

import com.rafael.budgeting.domain.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        @NotBlank String description,
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type,
        String category,
        LocalDate date
) {
}
