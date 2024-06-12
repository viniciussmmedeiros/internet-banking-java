package com.internetbanking.accountapi.service.authApi;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterRequest {

    @NotBlank(message = "AppId cannot be blank.")
    private UUID appId;

    @NotBlank(message = "Login cannot be blank.")
    private String login;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotBlank(message = "Role cannot be blank.")
    private String role;
}
