package com.internetbanking.accountapi.controller.account.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountData {

    private UUID id;

    private BigDecimal balance;

    private boolean isBlocked;

    public AccountData(UUID id) {
        this.id = id;
    }
}