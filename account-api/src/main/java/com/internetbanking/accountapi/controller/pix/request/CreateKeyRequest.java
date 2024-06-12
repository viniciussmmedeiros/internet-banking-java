package com.internetbanking.accountapi.controller.pix.request;

import com.internetbanking.accountapi.enums.PixKeyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateKeyRequest {

    private UUID accountId;

    private PixKeyType type;

    private String value;
}
