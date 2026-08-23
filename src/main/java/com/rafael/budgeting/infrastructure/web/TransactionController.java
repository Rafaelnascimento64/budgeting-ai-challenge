package com.rafael.budgeting.infrastructure.web;

import com.rafael.budgeting.application.CreateTransactionUseCase;
import com.rafael.budgeting.application.GetBalanceByCategoryUseCase;
import com.rafael.budgeting.application.ListTransactionsUseCase;
import com.rafael.budgeting.application.dto.CategoryBalance;
import com.rafael.budgeting.application.dto.CreateTransactionInput;
import com.rafael.budgeting.domain.Transaction;
import com.rafael.budgeting.domain.TransactionType;
import com.rafael.budgeting.infrastructure.web.dto.CreateTransactionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetBalanceByCategoryUseCase getBalanceByCategoryUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                  ListTransactionsUseCase listTransactionsUseCase,
                                  GetBalanceByCategoryUseCase getBalanceByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getBalanceByCategoryUseCase = getBalanceByCategoryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody CreateTransactionRequest request) {
        return createTransactionUseCase.execute(new CreateTransactionInput(
                request.description(), request.amount(), request.type(), request.category(), request.date()
        ));
    }

    @GetMapping
    public List<Transaction> list(@RequestParam(required = false) String category,
                                   @RequestParam(required = false) TransactionType type) {
        return listTransactionsUseCase.execute(category, type);
    }

    // Novo endpoint REST criado como parte da melhoria: expõe a mesma
    // consulta de saldo por categoria usada pela ferramenta de IA.
    @GetMapping("/balance-by-category")
    public List<CategoryBalance> balanceByCategory() {
        return getBalanceByCategoryUseCase.execute();
    }

    @GetMapping("/balance-by-category/{category}")
    public CategoryBalance balanceForCategory(@PathVariable String category) {
        return getBalanceByCategoryUseCase.executeForCategory(category);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
