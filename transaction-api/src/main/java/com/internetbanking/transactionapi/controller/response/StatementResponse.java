package com.internetbanking.transactionapi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatementResponse {

    private ArrayList<StatementTransactionResponse> transactions;

    private LocalDateTime date;
}
