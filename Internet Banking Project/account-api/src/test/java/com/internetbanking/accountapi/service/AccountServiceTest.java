package com.internetbanking.accountapi.service;

import com.internetbanking.accountapi.controller.account.request.CreateAccountRequest;
import com.internetbanking.accountapi.controller.account.request.LoginRequest;
import com.internetbanking.accountapi.controller.account.response.AccountData;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private KafkaClientService kafkaClientService;

    @InjectMocks
    private AccountService accountService = spy(AccountService.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(accountService, "appId", "89cfd07a-dbfb-4263-b500-11477eabd2cc");
    }

    @Test
    @DisplayName("User should be able to login successfully")
    public void userShouldLoginSuccessfully() {
        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setBlocked(false);

        LoginRequest request = new LoginRequest("", "", "");
        doReturn(Optional.of(account)).when(accountRepository).findByBranchAndAccountNumber(request.getBranch(), request.getAccountNumber());

        AuthLoginResponse authResponse = new AuthLoginResponse();
        authResponse.setToken("");

        doReturn(authResponse).when(accountService).authLogin(any(AuthLoginRequest.class));

        LoginResponse response = accountService.login(request);

        Assertions.assertEquals(account.getId(), response.getAccountId());
        Assertions.assertNotNull(response.getToken());
    }

    @Test
    @DisplayName("Login should throw exception if user not found")
    public void exceptionOnLoginWhenUserNotFound() {
        LoginRequest request = new LoginRequest("", "", "");

        doReturn(Optional.empty()).when(accountRepository).findByBranchAndAccountNumber(request.getBranch(), request.getAccountNumber());

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> accountService.login(request));
        Assertions.assertEquals("401 UNAUTHORIZED \"Account not found.\"", exception.getMessage());
    }

    @Test
    @DisplayName("User should not be able to login if the account is blocked")
    public void exceptionOnLoginIfAccountIsBlocked() {
        Account account = new Account();
        account.setBlocked(true);

        LoginRequest request = new LoginRequest("", "", "");

        doReturn(Optional.of(account)).when(accountRepository).findByBranchAndAccountNumber(request.getBranch(), request.getAccountNumber());

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> accountService.login(request));
        Assertions.assertEquals("403 FORBIDDEN \"Account is blocked.\"", exception.getMessage());
    }

    @Test
    @DisplayName("User should be able to create account successfully")
    public void userShouldRegisterSuccessfully() {
        CreateAccountRequest request = new CreateAccountRequest("", "", "email@email.com", "07207120095", "");

        doReturn(new AuthRegisterResponse()).when(accountService).authRegister(any(AuthRegisterRequest.class));
        doReturn(new Account()).when(accountRepository).save(any(Account.class));

        accountService.createAccount(request);
    }

    @Test
    @DisplayName("Get account data should return response correctly")
    public void shouldGetDataCorrectly() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .balance(BigDecimal.TEN)
                .isBlocked(false)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountData accountData = accountService.getData(accountId);
        Assertions.assertEquals(accountId, accountData.getId());
        Assertions.assertEquals(account.getBalance(), accountData.getBalance());
        Assertions.assertEquals(account.isBlocked(), accountData.isBlocked());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("Should update balance correctly when payerId is provided")
    public void shouldUpdateBalanceWithPayerId() {
        UUID payerId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Account account = Account.builder()
                .id(payerId)
                .balance(BigDecimal.ZERO)
                .build();

        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .payerId(payerId)
                .amount(amount)
                .updateType(UpdateBalanceType.SUM)
                .build();

        when(accountRepository.findById(payerId)).thenReturn(Optional.of(account));

        AccountData result = accountService.handleBalanceUpdate(request);

        Assertions.assertEquals(payerId, result.getId());
        Assertions.assertEquals(amount, account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    @DisplayName("Should throw exception when payerId account not found")
    public void shouldThrowExceptionWhenPayerIdNotFound() {
        UUID payerId = UUID.randomUUID();
        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .payerId(payerId)
                .amount(BigDecimal.valueOf(100))
                .updateType(UpdateBalanceType.SUM)
                .build();

        when(accountRepository.findById(payerId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> accountService.handleBalanceUpdate(request));
        Assertions.assertEquals("404 NOT_FOUND \"Account not found.\"", exception.getMessage());
    }

    @Test
    @DisplayName("Should update balance correctly when accountNumber and branch are provided")
    public void shouldUpdateBalanceWithAccountNumberAndBranch() {
        String branch = "1234";
        String accountNumber = "123456";
        BigDecimal amount = BigDecimal.valueOf(100);
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .balance(BigDecimal.ZERO)
                .branch(branch)
                .accountNumber(accountNumber)
                .build();

        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .branch(branch)
                .accountNumber(accountNumber)
                .amount(amount)
                .updateType(UpdateBalanceType.SUM)
                .build();

        when(accountRepository.findByBranchAndAccountNumber(branch, accountNumber)).thenReturn(Optional.of(account));

        AccountData result = accountService.handleBalanceUpdate(request);

        Assertions.assertEquals(account.getId(), result.getId());
        Assertions.assertEquals(amount, account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    @DisplayName("Should throw exception when accountNumber and branch account not found")
    public void shouldThrowExceptionWhenAccountNumberAndBranchNotFound() {
        String branch = "1234";
        String accountNumber = "123456";
        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .branch(branch)
                .accountNumber(accountNumber)
                .amount(BigDecimal.valueOf(100))
                .updateType(UpdateBalanceType.SUM)
                .build();

        when(accountRepository.findByBranchAndAccountNumber(branch, accountNumber)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> accountService.handleBalanceUpdate(request));
        Assertions.assertEquals("404 NOT_FOUND \"Account not found.\"", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle SUM_SUB update type correctly")
    public void shouldHandleSumSubUpdateType() {
        UUID payerId = UUID.randomUUID();
        String branch = "1234";
        String accountNumber = "123456";
        BigDecimal amount = BigDecimal.valueOf(100);

        Account payerAccount = Account.builder()
                .id(payerId)
                .balance(BigDecimal.valueOf(200))
                .build();

        Account payeeAccount = Account.builder()
                .id(UUID.randomUUID())
                .balance(BigDecimal.ZERO)
                .branch(branch)
                .accountNumber(accountNumber)
                .build();

        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .payerId(payerId)
                .branch(branch)
                .accountNumber(accountNumber)
                .amount(amount)
                .updateType(UpdateBalanceType.SUM_SUB)
                .build();

        when(accountRepository.findById(payerId)).thenReturn(Optional.of(payerAccount));
        when(accountRepository.findByBranchAndAccountNumber(branch, accountNumber)).thenReturn(Optional.of(payeeAccount));

        accountService.updateBalance(request);

        Assertions.assertEquals(BigDecimal.valueOf(100), payerAccount.getBalance());
        Assertions.assertEquals(amount, payeeAccount.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(kafkaClientService, times(1)).produceCompleteTransactionRequest(any(CompleteTransactionRequest.class));
    }

    @Test
    @DisplayName("Should handle single balance update SUM correctly")
    public void shouldHandleSingleBalanceUpdateSum() {
        UUID payerId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Account account = Account.builder()
                .id(payerId)
                .balance(BigDecimal.ZERO)
                .build();

        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .payerId(payerId)
                .amount(amount)
                .updateType(UpdateBalanceType.SUM)
                .build();

        when(accountRepository.findById(payerId)).thenReturn(Optional.of(account));

        accountService.updateBalance(request);

        Assertions.assertEquals(amount, account.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(kafkaClientService, times(1)).produceCompleteTransactionRequest(any(CompleteTransactionRequest.class));
    }

    @Test
    @DisplayName("Should handle single balance update SUB correctly")
    public void shouldHandleSingleBalanceUpdateSub() {
        UUID payerId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Account account = Account.builder()
                .id(payerId)
                .balance(BigDecimal.valueOf(200))
                .build();

        BalanceUpdateRequest request = new BalanceUpdateRequest.Builder()
                .payerId(payerId)
                .amount(amount)
                .updateType(UpdateBalanceType.SUB)
                .build();

        when(accountRepository.findById(payerId)).thenReturn(Optional.of(account));

        accountService.updateBalance(request);

        Assertions.assertEquals(BigDecimal.valueOf(100), account.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(kafkaClientService, times(1)).produceCompleteTransactionRequest(any(CompleteTransactionRequest.class));
    }
}
