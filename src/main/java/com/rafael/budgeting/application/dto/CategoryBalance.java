package com.rafael.budgeting.application.dto;

import java.math.BigDecimal;

public record CategoryBalance(
        String category,
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldo,
        long quantidadeTransacoes
) {
}
