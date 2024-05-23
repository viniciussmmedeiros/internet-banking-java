package com.internetbanking.accountapi.service.authApi;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginRequest {

    private String appId;

    private String login;

    private String password;
}
