package com.internetbanking.transactionapi.controller;

import com.internetbanking.transactionapi.controller.request.DepositRequest;
import com.internetbanking.transactionapi.controller.request.PayBilletRequest;
import com.internetbanking.transactionapi.controller.request.TransferRequest;
import com.internetbanking.transactionapi.controller.response.StatementResponse;
import com.internetbanking.transactionapi.kafka.CompleteTransactionRequest;
import com.internetbanking.transactionapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Transaction Controller")
@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Handles an intra-bank transfer.", method = "POST")
    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransferRequest request) {
        transactionService.transferIntraBank(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Handles a billet payment.", method = "POST")
    @PostMapping("/pay-billet")
    public void payBillet(@Valid @RequestBody PayBilletRequest request) {
        transactionService.payBillet(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Handles a deposit in the user's own account.", method = "POST")
    @PostMapping("/deposit")
    public void deposit(@Valid @RequestBody DepositRequest request) {
        transactionService.deposit(request);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Returns a statement containing the user's transactions.", method = "GET")
    @GetMapping("/{accountId}/get-statement")
    public StatementResponse getStatement(@PathVariable UUID accountId) {
        return transactionService.getStatement(accountId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Complete a transaction with the kafka-given data.", method = "POST")
    @PostMapping("/complete-transaction")
    public void completeTransaction(@Valid @RequestBody CompleteTransactionRequest request) {
        transactionService.completeTransaction(request);
    }
}
