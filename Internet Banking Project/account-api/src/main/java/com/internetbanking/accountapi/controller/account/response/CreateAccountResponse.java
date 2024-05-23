package com.internetbanking.accountapi.controller.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CreateAccountResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String cpf;

    private BigDecimal balance;

    private String branch;

    private String accountNumber;

    private String password;

    private String token;
}
