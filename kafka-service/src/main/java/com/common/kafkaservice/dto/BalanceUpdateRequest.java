package com.common.kafkaservice.dto;

import com.common.kafkaservice.enums.TransactionType;
import com.common.kafkaservice.enums.UpdateBalanceType;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@ToString
public class BalanceUpdateRequest {
    private UUID payerId;

    private String accountNumber;

    private String branch;

    private BigDecimal amount;

    private UpdateBalanceType updateType;

    private TransactionType transactionType;
}

