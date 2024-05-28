package com.internetbanking.transactionapi.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequest extends TransactionRequest {

    @NotNull(message = "Amount cannot be null.")
    private BigDecimal amount;
}
