package com.internetbanking.transactionapi.service.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountData {

    private UUID id;

    private BigDecimal balance;

    private boolean isBlocked;

    public AccountData(UUID id) {
        this.id = id;
    }
}
