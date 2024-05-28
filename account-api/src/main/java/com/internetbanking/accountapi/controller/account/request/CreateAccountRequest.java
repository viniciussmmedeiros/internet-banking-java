package com.internetbanking.accountapi.controller.account.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@AllArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @Email
    @NotBlank(message = "Email cannot be blank.")
    private String email;

    @CPF
    @NotBlank(message = "CPF cannot be blank.")
    private String cpf;

    @NotBlank(message = "Branch cannot be blank.")
    private String branch;
}
