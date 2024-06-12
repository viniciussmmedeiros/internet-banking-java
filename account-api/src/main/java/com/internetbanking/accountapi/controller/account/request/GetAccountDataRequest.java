package com.internetbanking.accountapi.controller.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetAccountDataRequest {

    private Long financialInstitutionId;

    private String accountNumber;

    private String branch;

    private String pixKey;
}
