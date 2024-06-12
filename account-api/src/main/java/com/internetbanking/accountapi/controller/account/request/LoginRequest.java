package com.internetbanking.accountapi.controller.account.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Account number cannot be blank.")
    private String accountNumber;

    @NotBlank(message = "Branch cannot be blank.")
    private String branch;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotNull(message = "Financial Institution Id cannot be blank.")
    private Long financialInstitutionId;
}
