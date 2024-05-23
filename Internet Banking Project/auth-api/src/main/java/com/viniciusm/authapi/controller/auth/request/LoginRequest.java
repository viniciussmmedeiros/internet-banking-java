package com.viniciusm.authapi.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = "Application id cannot be null.")
    private UUID appId;

    @NotBlank(message = "Login cannot be blank.")
    private String login;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
