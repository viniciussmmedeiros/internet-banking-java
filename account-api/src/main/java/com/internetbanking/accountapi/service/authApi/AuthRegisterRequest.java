package com.internetbanking.accountapi.service.authApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterRequest {

    private UUID appId;

    private String login;

    private String password;

    private final String role = "COMMON";
}
