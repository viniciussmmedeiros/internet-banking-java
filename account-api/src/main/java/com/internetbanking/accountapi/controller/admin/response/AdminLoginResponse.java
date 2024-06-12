package com.internetbanking.accountapi.controller.admin.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminLoginResponse {

    private String id;

    private String token;

    private LocalDateTime date;
}
