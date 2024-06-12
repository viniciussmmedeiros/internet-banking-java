package com.internetbanking.accountapi.controller.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccountTransactionDataResponse {

    public String fullName;

    public String cpf;

    public String financialInstitution;
}
