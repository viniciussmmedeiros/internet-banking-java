package com.internetbanking.accountapi.controller.admin.request;

import lombok.Data;

@Data
public class CreateAdminRequest {

    private String login;

    private String password;

    private Long financialInstitutionId;
}
