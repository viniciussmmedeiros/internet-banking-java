package com.internetbanking.accountapi.controller.account;

import com.internetbanking.accountapi.controller.account.request.CompleteAccountCreationRequest;
import com.internetbanking.accountapi.controller.account.request.GetAccountDataRequest;
import com.internetbanking.accountapi.controller.account.request.InitiateAccountCreationRequest;
import com.internetbanking.accountapi.controller.account.request.LoginRequest;
import com.internetbanking.accountapi.controller.account.response.AccountData;
import com.internetbanking.accountapi.controller.account.response.AccountTransactionDataResponse;
import com.internetbanking.accountapi.controller.account.response.LoginResponse;
import com.internetbanking.accountapi.kafka.BalanceUpdateRequest;
import com.internetbanking.accountapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Account Controller", description = "Handles account operations such as registration and login.")
@RestController
@RequestMapping("accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Logs in to an account and returns a token along with its data.", method = "POST")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return accountService.login(request);
    }

    @Operation(summary = "Provides basic account balance data to be consumed by the Transaction API.", method = "GET")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{accountId}/get-balance-data")
    public AccountData getBalanceData(@PathVariable UUID accountId) {
       return accountService.getBalanceData(accountId);
    }

    @Operation(summary = "Provides basic account data to be consumed by the Transaction API.", method = "GET")
    @PostMapping("{accountId}/get-data")
    public AccountTransactionDataResponse getAccountData(@RequestBody GetAccountDataRequest request) {
        return accountService.getAccountData(request);
    }

    @Operation(summary = "Updates the balance of an account based on the required operation.", method = "PUT")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/update-balance")
    public void updateBalance(@Valid @RequestBody BalanceUpdateRequest request) {
        accountService.updateBalance(request);
    }

    @Operation(summary = "Initiates the process of creating a new account.", method = "POST")
    @PostMapping("/initiate-account-creation")
    public void initiateAccountCreation(@Valid @RequestBody InitiateAccountCreationRequest request) {
        accountService.initiateAccountCreation(request);
    }

    @Operation(summary = "Completes the process of creating a new account.", method = "POST")
    @RequestMapping(value = "/complete-account-creation", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) //, consumes = { "multipart/form-data" })
    public void completeAccountCreation(@RequestPart CompleteAccountCreationRequest request,
                                        @RequestPart MultipartFile selfieDocument,
                                        @RequestPart MultipartFile proofOfAddress) {
        accountService.completeAccountCreation(request, selfieDocument, proofOfAddress);
    }

    @Operation(summary = "Verifies an email address using the provided token.", method = "GET")
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        return accountService.verifyEmail(token);
    }
}
