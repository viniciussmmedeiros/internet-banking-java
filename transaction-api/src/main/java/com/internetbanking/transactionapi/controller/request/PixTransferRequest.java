package com.internetbanking.transactionapi.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PixTransferRequest {

    private UUID payerId;

    private String pixKey;

    private BigDecimal amount;
}
