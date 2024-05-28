package com.internetbanking.accountapi.controller.account;

import com.internetbanking.accountapi.controller.account.request.CreateAccountRequest;
import com.internetbanking.accountapi.controller.account.request.LoginRequest;
import com.internetbanking.accountapi.controller.account.response.AccountData;
import com.internetbanking.accountapi.controller.account.response.CreateAccountResponse;
import com.internetbanking.accountapi.controller.account.response.LoginResponse;
import com.internetbanking.accountapi.kafka.BalanceUpdateRequest;
import com.internetbanking.accountapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Account Controller")
@RestController
@RequestMapping("accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Creates a new account and returns its data.", method = "POST")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountResponse account = accountService.createAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @Operation(summary = "Logs in to an account and returns a token and its data.", method = "POST")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return accountService.login(request);
    }

    @Operation(summary = "Provides basic account data to be consumed by the Transaction API.", method = "GET")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{accountId}/get-data")
    public AccountData getData(@PathVariable UUID accountId) {
       return accountService.getData(accountId);
    }

    @Operation(summary = "Updates the balance of an account based on the required operation.", method = "PUT")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/update-balance")
    public void updateBalance(@Valid @RequestBody BalanceUpdateRequest request) {
        accountService.updateBalance(request);
    }
}
