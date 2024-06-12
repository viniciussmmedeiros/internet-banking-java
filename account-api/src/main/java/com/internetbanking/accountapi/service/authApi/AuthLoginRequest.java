package com.internetbanking.accountapi.service.authApi;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginRequest {

    @NotBlank(message = "AppId cannot be blank.")
    private String appId;

    @NotBlank(message = "Login cannot be blank.")
    private String login;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
