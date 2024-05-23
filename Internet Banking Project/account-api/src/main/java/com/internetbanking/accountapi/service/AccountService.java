package com.internetbanking.accountapi.service;

import com.internetbanking.accountapi.controller.account.request.CreateAccountRequest;
import com.internetbanking.accountapi.controller.account.request.LoginRequest;
import com.internetbanking.accountapi.controller.account.response.AccountData;
import com.internetbanking.accountapi.controller.account.response.CreateAccountResponse;
import com.internetbanking.accountapi.controller.account.response.LoginResponse;
import com.internetbanking.accountapi.enums.UpdateBalanceType;
import com.internetbanking.accountapi.kafka.BalanceUpdateRequest;
import com.internetbanking.accountapi.kafka.CompleteTransactionRequest;
import com.internetbanking.accountapi.kafka.KafkaClientService;
import com.internetbanking.accountapi.model.Account;
import com.internetbanking.accountapi.repository.AccountRepository;
import com.internetbanking.accountapi.service.authApi.AuthLoginRequest;
import com.internetbanking.accountapi.service.authApi.AuthLoginResponse;
import com.internetbanking.accountapi.service.authApi.AuthRegisterRequest;
import com.internetbanking.accountapi.service.authApi.AuthRegisterResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaClientService kafkaClientService;

    @Value("${auth.url}")
    private String authUrl;

    @Value("${auth.appId}")
    private String appId;

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        String accountNumber = String.valueOf((int) (Math.random() * Math.pow(10, 5)));

        Account account = Account
                .builder()
                .id(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .balance(BigDecimal.ZERO)
                .branch(request.getBranch())
                .accountNumber(accountNumber)
                .isBlocked(false)
                .build();

        String password = String.valueOf((int) (Math.random() * Math.pow(10, 6)));

        // call auth service -- (accountNumber+branch | password)
        String login = accountNumber + "+" + password;

        AuthRegisterResponse authResponse = null;

        try {
            authResponse = authRegister(new AuthRegisterRequest(UUID.fromString(appId), login, password));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }

        Account createdAccount = accountRepository.save(account);

        CreateAccountResponse response = CreateAccountResponse
                .builder()
                .id(createdAccount.getId())
                .firstName(createdAccount.getFirstName())
                .lastName(createdAccount.getLastName())
                .cpf(createdAccount.getCpf())
                .balance(createdAccount.getBalance())
                .branch(createdAccount.getBranch())
                .accountNumber(createdAccount.getAccountNumber())
                .password(password)
                .build();

        if (authResponse != null) {
            response.setToken(authResponse.getToken());
        }

        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByBranchAndAccountNumber(request.getBranch(), request.getAccountNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found."));

        if (account.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is blocked.");
        }

        String login = request.getAccountNumber() + "+" + request.getPassword();

        AuthLoginRequest authLoginRequest = new AuthLoginRequest(appId, login, request.getPassword());

        try {
            AuthLoginResponse authLoginResponse = authLogin(authLoginRequest);

            return new LoginResponse(account.getId(), account.getFirstName(), authLoginResponse.getToken());
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    public AccountData getData(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        return new AccountData(account.getId(), account.getBalance(), account.isBlocked());
    }

    @Transactional
    public AccountData handleBalanceUpdate(BalanceUpdateRequest request) {
        Account account = null;

        if(request.getPayerId() != null) {
            account = accountRepository.findById(request.getPayerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
        }

        if(request.getAccountNumber() != null && request.getBranch() != null) {
            account = accountRepository.findByBranchAndAccountNumber(request.getBranch(), request.getAccountNumber())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
        }

        if(account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        }

        if(request.getUpdateType() == UpdateBalanceType.SUM) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }

        if(request.getUpdateType() == UpdateBalanceType.SUB) {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }

        accountRepository.save(account);

        return new AccountData(account.getId());
    }

    public void updateBalance(BalanceUpdateRequest request) {
        AccountData accountData = null;

        if(request.getUpdateType() == UpdateBalanceType.SUM_SUB) {
            accountData = this.handleBalanceUpdate(new BalanceUpdateRequest.Builder()
                            .accountNumber(request.getAccountNumber())
                            .branch(request.getBranch())
                            .amount(request.getAmount())
                            .updateType(UpdateBalanceType.SUM)
                            .build());

            this.handleBalanceUpdate(new BalanceUpdateRequest.Builder()
                    .payerId(request.getPayerId())
                    .amount(request.getAmount())
                    .updateType(UpdateBalanceType.SUB)
                    .build());
        } else {
            this.handleBalanceUpdate(new BalanceUpdateRequest.Builder()
                    .payerId(request.getPayerId())
                    .amount(request.getAmount())
                    .updateType(request.getUpdateType())
                    .build());
        }

        CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                request.getPayerId(),
                request.getAmount(),
                request.getTransactionType()
        );

        if(accountData != null) {
            completeTransactionRequest.setPayeeId(accountData.getId());
        }

        kafkaClientService.produceCompleteTransactionRequest(completeTransactionRequest);
    }

    public AuthLoginResponse authLogin(AuthLoginRequest request) {
        String url = authUrl + "/auth/login";

        return WebClient.create()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(AuthLoginResponse.class)
                .block();
    }

    public AuthRegisterResponse authRegister(AuthRegisterRequest request) {
        String url = authUrl + "/auth/register";

        return WebClient.create()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(AuthRegisterResponse.class)
                .block();
    }
}
