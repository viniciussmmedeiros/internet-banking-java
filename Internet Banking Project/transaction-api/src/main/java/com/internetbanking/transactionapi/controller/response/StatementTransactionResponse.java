package com.internetbanking.transactionapi.controller.response;

import com.internetbanking.transactionapi.enums.TransactionDirection;
import com.internetbanking.transactionapi.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatementTransactionResponse {

    private Long id;

    private UUID accountId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionDirection direction;
}
