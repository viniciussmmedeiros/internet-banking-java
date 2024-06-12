package com.internetbanking.transactionapi.kafka;

import com.internetbanking.transactionapi.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CompleteTransactionRequest {
    private UUID payerId;

    private BigDecimal amount;

    private TransactionType type;

    private UUID payeeId;

    private String payeeName;

    private String payerName;
}
