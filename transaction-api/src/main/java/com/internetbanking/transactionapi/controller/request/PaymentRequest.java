package com.internetbanking.transactionapi.controller.request;

import com.internetbanking.transactionapi.enums.PaymentType;
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
public class PaymentRequest extends TransactionRequest {

    @NotBlank(message = "Billet code cannot be blank.")
    private String code;

    private PaymentType type;

    public PaymentRequest(UUID payerId, String code, PaymentType type) {
        super(payerId);
        this.code = code;
        this.type = type;
    }
}
