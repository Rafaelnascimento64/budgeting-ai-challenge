package com.rafael.budgeting.application;

import com.rafael.budgeting.application.dto.CategoryBalance;
import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionRepository;
import com.rafael.budgeting.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetBalanceByCategoryUseCaseTest {

    @Test
    void deveCalcularSaldoAgrupadoPorCategoria() {
        List<Transaction> transactions = List.of(
                Transaction.create("Salário", new BigDecimal("3000"), TransactionType.RECEITA, "SALARIO", LocalDate.now()),
                Transaction.create("Mercado", new BigDecimal("400"), TransactionType.DESPESA, "ALIMENTACAO", LocalDate.now()),
                Transaction.create("Restaurante", new BigDecimal("150"), TransactionType.DESPESA, "ALIMENTACAO", LocalDate.now())
        );

        TransactionRepository fakeRepository = new InMemoryFakeRepository(transactions);
        GetBalanceByCategoryUseCase useCase = new GetBalanceByCategoryUseCase(fakeRepository);

        List<CategoryBalance> result = useCase.execute();

        CategoryBalance alimentacao = result.stream()
                .filter(b -> b.category().equals("ALIMENTACAO"))
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("550"), alimentacao.totalDespesas());
        assertEquals(new BigDecimal("0").subtract(new BigDecimal("550")), alimentacao.saldo());
        assertEquals(2, alimentacao.quantidadeTransacoes());

        CategoryBalance salario = result.stream()
                .filter(b -> b.category().equals("SALARIO"))
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("3000"), salario.totalReceitas());
        assertEquals(new BigDecimal("3000"), salario.saldo());
    }

    private static class InMemoryFakeRepository implements TransactionRepository {
        private final List<Transaction> data;

        InMemoryFakeRepository(List<Transaction> data) {
            this.data = data;
        }

        @Override
        public Transaction save(Transaction transaction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Transaction> findAll() {
            return data;
        }

        @Override
        public List<Transaction> findByCategory(String category) {
            return data.stream().filter(t -> t.category().equals(category)).collect(Collectors.toList());
        }

        @Override
        public List<Transaction> findByType(TransactionType type) {
            return data.stream().filter(t -> t.type() == type).collect(Collectors.toList());
        }
    }
}
