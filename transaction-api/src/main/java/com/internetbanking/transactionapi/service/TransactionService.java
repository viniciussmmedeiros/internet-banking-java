package com.internetbanking.transactionapi.service;

import com.internetbanking.transactionapi.controller.request.DepositRequest;
import com.internetbanking.transactionapi.controller.request.PayBilletRequest;
import com.internetbanking.transactionapi.controller.request.TransferRequest;
import com.internetbanking.transactionapi.controller.response.StatementResponse;
import com.internetbanking.transactionapi.controller.response.StatementTransactionResponse;
import com.internetbanking.transactionapi.enums.TransactionDirection;
import com.internetbanking.transactionapi.enums.TransactionType;
import com.internetbanking.transactionapi.enums.UpdateBalanceType;
import com.internetbanking.transactionapi.kafka.BalanceUpdateRequest;
import com.internetbanking.transactionapi.kafka.CompleteTransactionRequest;
import com.internetbanking.transactionapi.kafka.KafkaServiceClient;
import com.internetbanking.transactionapi.model.Transaction;
import com.internetbanking.transactionapi.repository.TransactionRepository;
import com.internetbanking.transactionapi.service.account.AccountData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @Autowired
    private KafkaServiceClient kafkaServiceClient;

    @Value("${account.api.url}")
    private String accountApiUrl;

    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    public void transferIntraBank(TransferRequest request) {
        AccountData payerAccount;

        try {
            payerAccount = getAccountDataById(request.getPayerId());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payer account not found.", ex);
        }

        if (payerAccount.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is blocked, cannot complete transfer.");
        }

        if (payerAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance to complete transfer.");
        }

        BalanceUpdateRequest balanceUpdateRequest = new BalanceUpdateRequest.Builder()
                .payerId(payerAccount.getId())
                .accountNumber(request.getAccountNumber())
                .branch(request.getBranch())
                .amount(request.getAmount())
                .updateType(UpdateBalanceType.SUM_SUB)
                .transactionType(TransactionType.INTRA_BANK_TRANSFER)
                .build();

        kafkaServiceClient.produceBalanceUpdateRequest(balanceUpdateRequest);
    }

    @Transactional
    public void payBillet(PayBilletRequest request) {
        AccountData account = getAccountDataById(request.getPayerId());
        double amount = getBilletMockData(request.getBilletCode());

        if(account.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is blocked, cannot complete billet payment.");
        }

        if(account.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance to pay the billet.");
        }

        BalanceUpdateRequest transactionMessage = new BalanceUpdateRequest.Builder()
                .payerId(account.getId())
                .amount(BigDecimal.valueOf(amount))
                .updateType(UpdateBalanceType.SUB)
                .transactionType(TransactionType.BILLET_PAYMENT)
                .build();

        kafkaServiceClient.produceBalanceUpdateRequest(transactionMessage);
    }

    public void deposit(DepositRequest request) {
        BalanceUpdateRequest balanceRequest = new BalanceUpdateRequest.Builder()
                .payerId(request.getPayerId())
                .amount(request.getAmount())
                .updateType(UpdateBalanceType.SUM)
                .transactionType(TransactionType.DEPOSIT)
                .build();

        kafkaServiceClient.produceBalanceUpdateRequest(balanceRequest);
    }

    public StatementResponse getStatement(UUID accountId) {
        ArrayList<Transaction> result = transactionRepository.findAllByPayerIdOrPayeeId(accountId, accountId);

        LocalDateTime date = LocalDateTime.now();

        ArrayList<StatementTransactionResponse> transactionDTOs = new ArrayList<>();

        for (Transaction transaction : result) {
            StatementTransactionResponse transactionDTO = new StatementTransactionResponse();
            transactionDTO.setId(transaction.getId());
            transactionDTO.setAmount(transaction.getAmount());
            transactionDTO.setType(transaction.getType());

            if (accountId.equals(transaction.getPayerId())) {
                transactionDTO.setAccountId(transaction.getPayerId());

                if(transaction.getType() == TransactionType.DEPOSIT) {
                    transactionDTO.setDirection(TransactionDirection.RECEIVED);
                } else {
                    transactionDTO.setDirection(TransactionDirection.SENT);
                }
            } else if (accountId.equals(transaction.getPayeeId())) {
                transactionDTO.setAccountId(transaction.getPayeeId());
                transactionDTO.setDirection(TransactionDirection.RECEIVED);
            }

            transactionDTOs.add(transactionDTO);
        }

        return new StatementResponse(transactionDTOs, date);
    }

    public AccountData getAccountDataById(UUID accountId) {
        String url = accountApiUrl + "/accounts/" + accountId.toString() + "/get-data";

        return WebClient.create()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(AccountData.class)
                .block();
    }

    public void completeTransaction(CompleteTransactionRequest request) {
        Transaction transaction = new Transaction(request.getPayerId(), request.getAmount(), request.getType());

        if(request.getPayeeId() != null) {
            transaction.setPayeeId(request.getPayeeId());
        }

        transactionRepository.save(transaction);
    }

    public double getBilletMockData(String billetCode) {
        return 300;
    }
}
