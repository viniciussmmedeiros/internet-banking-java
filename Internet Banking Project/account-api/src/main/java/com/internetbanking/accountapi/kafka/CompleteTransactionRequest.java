package com.internetbanking.accountapi.kafka;

import com.internetbanking.accountapi.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTransactionRequest {
    private UUID payerId;

    private BigDecimal amount;

    private TransactionType type;

    private UUID payeeId;

    public CompleteTransactionRequest(UUID payerId, BigDecimal amount, TransactionType type) {
        this.payerId = payerId;
        this.amount = amount;
        this.type = type;
    }
}
