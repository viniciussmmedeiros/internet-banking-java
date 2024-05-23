package com.internetbanking.accountapi.controller.account.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private UUID accountId;

    private String firstName;

    private String token;
}
