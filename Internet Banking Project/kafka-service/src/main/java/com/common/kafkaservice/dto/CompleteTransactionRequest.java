package com.common.kafkaservice.dto;

import com.common.kafkaservice.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CompleteTransactionRequest {
    private UUID payerId;

    private BigDecimal amount;

    private TransactionType type;

    private UUID payeeId;
}
