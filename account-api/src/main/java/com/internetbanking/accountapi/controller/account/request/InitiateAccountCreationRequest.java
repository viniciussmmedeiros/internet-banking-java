package com.internetbanking.accountapi.controller.account.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitiateAccountCreationRequest {

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @NotBlank(message = "Email cannot be blank.")
    @Email
    private String email;

    @NotBlank(message = "CPF cannot be blank.")
    private String cpf;

    @NotNull(message = "Financial Institution Id cannot be null.")
    private Long financialInstitutionId;
}
