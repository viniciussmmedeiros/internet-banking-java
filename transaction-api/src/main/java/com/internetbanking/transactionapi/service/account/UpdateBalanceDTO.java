package com.internetbanking.transactionapi.service.account;

import com.internetbanking.transactionapi.enums.UpdateBalanceType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateBalanceDTO {
    private UUID accountId;

    private String accountNumber;

    private String branch;

    private BigDecimal amount;

    private UpdateBalanceType type;
}
