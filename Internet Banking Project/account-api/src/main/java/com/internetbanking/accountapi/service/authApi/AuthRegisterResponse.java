package com.internetbanking.accountapi.service.authApi;

import lombok.Getter;

@Getter
public class AuthRegisterResponse {

    private String login;

    private String password;

    private String role;

    private String token;
}
