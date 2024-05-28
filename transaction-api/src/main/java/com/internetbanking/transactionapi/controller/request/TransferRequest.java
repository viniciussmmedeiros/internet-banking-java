package com.internetbanking.transactionapi.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest extends TransactionRequest {

    @NotBlank(message = "Account number cannot be blank.")
    private String accountNumber;

    @NotBlank(message = "Branch cannot be blank.")
    private String branch;

    @NotNull(message = "Amount cannot be null.")
    private BigDecimal amount;

    public TransferRequest(UUID payerId, String accountNumber, String branch, BigDecimal amount) {
        super(payerId);
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.amount = amount;
    }
}
