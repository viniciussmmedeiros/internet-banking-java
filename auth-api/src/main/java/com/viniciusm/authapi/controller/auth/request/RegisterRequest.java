package com.viniciusm.authapi.controller.auth.request;

import com.viniciusm.authapi.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotNull(message = "Application id cannot be null.")
    private UUID appId;

    @NotBlank(message = "Login cannot be blank.")
    private String login;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotNull(message = "Role cannot be null.")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
