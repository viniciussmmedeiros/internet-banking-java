package com.internetbanking.accountapi.controller.account.request;

import com.internetbanking.accountapi.model.Address;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompleteAccountCreationRequest {

    @NotBlank(message = "CPF cannot be blank.")
    private String cpf;

    @NotBlank(message = "Branch cannot be blank.")
    private String branch;

    @Embedded
    private Address address;
}
