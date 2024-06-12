package com.internetbanking.accountapi.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {

    @NotBlank(message = "Street cannot be blank.")
    private String street;

    @NotBlank(message = "Number cannot be blank.")
    private String number;

    @NotBlank(message = "Apartment cannot be blank.")
    private String apartment;

    @NotBlank(message = "CEP cannot be blank.")
    private String cep;
}
