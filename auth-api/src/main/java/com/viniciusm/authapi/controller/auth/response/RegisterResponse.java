package com.viniciusm.authapi.controller.auth.response;

import com.viniciusm.authapi.enums.UserRole;
import com.viniciusm.authapi.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {

    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String token;

    public RegisterResponse(User savedUser, String token) {
        this.login = savedUser.getLogin();
        this.password = savedUser.getPassword();
        this.role = savedUser.getRole();
        this.token = token;
    }
}
