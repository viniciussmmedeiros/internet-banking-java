package com.internetbanking.transactionapi.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchAccountTransferRequest extends TransactionRequest {

    @NotBlank(message = "Account number cannot be blank.")
    private String accountNumber;

    @NotBlank(message = "Branch cannot be blank.")
    private String branch;

    @NotNull(message = "Amount cannot be null.")
    private BigDecimal amount;

    @NotNull(message = "Financial Institution Id cannot be null.")
    private Long financialInstitutionId;

    public BranchAccountTransferRequest(UUID payerId, String accountNumber, String branch, BigDecimal amount, Long financialInstitutionId) {
        super(payerId);
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.amount = amount;
        this.financialInstitutionId = financialInstitutionId;
    }
}
