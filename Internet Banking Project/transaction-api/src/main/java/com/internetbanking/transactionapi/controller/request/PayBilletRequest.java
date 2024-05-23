package com.internetbanking.transactionapi.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayBilletRequest extends TransactionRequest {

    @NotBlank(message = "Billet code cannot be blank.")
    private String billetCode;

    public PayBilletRequest(UUID payerId, String billetCode) {
        super(payerId);
        this.billetCode = billetCode;
    }
}
