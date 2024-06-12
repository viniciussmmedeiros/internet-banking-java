package com.internetbanking.transactionapi.controller;

import com.internetbanking.transactionapi.controller.request.BranchAccountTransferRequest;
import com.internetbanking.transactionapi.controller.request.PaymentRequest;
import com.internetbanking.transactionapi.controller.request.PixTransferRequest;
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
    @Operation(summary = "Handles an intra-bank transfer using branch and account information.", method = "POST")
    @PostMapping("/transfer-branch-account")
    public void transferBranchAccount(@Valid @RequestBody BranchAccountTransferRequest request) {
        transactionService.transferBankAccount(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Process a pix transfer.", method = "POST")
    @PostMapping("/transfer-pix")
    public void transferPix(@RequestBody PixTransferRequest request) {
        transactionService.transferPix(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Process a payment.", method = "POST")
    @PostMapping("/pay")
    public void pay(@Valid @RequestBody PaymentRequest request) {
        transactionService.pay(request);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Returns a statement containing the user's transactions.", method = "GET")
    @GetMapping("/{accountId}/get-statement")
    public StatementResponse getStatement(@PathVariable UUID accountId) {
        return transactionService.getStatement(accountId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Completes a transaction using data provided by Account Api.", method = "POST")
    @PostMapping("/complete-transaction")
    public void completeTransaction(@Valid @RequestBody CompleteTransactionRequest request) {
        transactionService.completeTransaction(request);
    }
}
