package com.viniciusm.authapi.controller.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private LocalDateTime date;
}
