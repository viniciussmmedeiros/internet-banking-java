package com.internetbanking.accountapi.service.authApi;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthLoginResponse {

    private String token;

    private LocalDateTime date;
}
