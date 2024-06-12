package com.internetbanking.transactionapi.service;

import com.internetbanking.transactionapi.controller.request.PaymentRequest;
import com.internetbanking.transactionapi.controller.request.BranchAccountTransferRequest;
import com.internetbanking.transactionapi.enums.PaymentType;
import com.internetbanking.transactionapi.kafka.BalanceUpdateRequest;
import com.internetbanking.transactionapi.kafka.KafkaServiceClient;
import com.internetbanking.transactionapi.repository.TransactionRepository;
import com.internetbanking.transactionapi.service.account.AccountData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaServiceClient kafkaServiceClient;

    @InjectMocks
    private TransactionService transactionService = spy(TransactionService.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("User should be able to request billet payment successfully")
    void shouldRequestPayBilletSuccessfully() {
        AccountData account = new AccountData(UUID.randomUUID(), BigDecimal.valueOf(1000), false);

        PaymentRequest request = new PaymentRequest(account.getId(), "", PaymentType.BILLET);

        doReturn(account).when(transactionService).getAccountDataById(request.getPayerId());

        doReturn(999.9).when(transactionService).getBilletMockData(request.getCode());

        transactionService.pay(request);

        verify(kafkaServiceClient, times(1)).produceBalanceUpdateRequest(any(BalanceUpdateRequest.class));
    }

    @Test
    @DisplayName("An exception should be thrown if user doesn't have enough balance to pay billet")
    void exceptionWhenNotEnoughBalancePayBillet() {
        AccountData account = new AccountData(UUID.randomUUID(), BigDecimal.valueOf(100), false);

        PaymentRequest request = new PaymentRequest(account.getId(), "", PaymentType.BILLET);

        doReturn(account).when(transactionService).getAccountDataById(request.getPayerId());
        doReturn(101.0).when(transactionService).getBilletMockData(request.getCode());

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> transactionService.pay(request));
        Assertions.assertEquals("400 BAD_REQUEST \"Not enough balance to perform the payment.\"", exception.getMessage());
        verifyNoInteractions(kafkaServiceClient);
    }

    @Test
    @DisplayName("An exception should be thrown when a blocked account tries to pay billet")
    void payBilletExceptionWhenAccountIsBlocked() {
        AccountData payerAccount = new AccountData(UUID.randomUUID(), BigDecimal.valueOf(1), true);

        PaymentRequest request = new PaymentRequest(payerAccount.getId(), "", PaymentType.BILLET);

        doReturn(payerAccount).when(transactionService).getAccountDataById(request.getPayerId());

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> transactionService.pay(request));
        Assertions.assertEquals(exception.getMessage(), "400 BAD_REQUEST \"Account is blocked, cannot complete payment.\"");
    }

//    @Test
//    @DisplayName("User should be able to transfer money to another account successfully")
//    void shouldTransferMoneyAnotherAccountSuccessfully() {
//        AccountData firstAccount = new AccountData(UUID.randomUUID(), BigDecimal.valueOf(1000), false);
//
//        BranchAccountTransferRequest request = new BranchAccountTransferRequest(
//                firstAccount.getId(), "123456","1234", BigDecimal.valueOf(20));
//
//        doReturn(firstAccount).when(transactionService).getAccountDataById(request.getPayerId());
//
//        transactionService.transferBankAccount(request);
//
//        verify(kafkaServiceClient, times(1)).produceBalanceUpdateRequest(any(BalanceUpdateRequest.class));
//    }

//    @Test
//    @DisplayName("An exception should be thrown if user doesn't have enough balance to transfer money")
//    void exceptionWhenNotEnoughBalanceWhenTransfer() {
//        AccountData payerAccount = new AccountData(UUID.randomUUID(), BigDecimal.valueOf(1000), false);
//
//        BranchAccountTransferRequest request = new BranchAccountTransferRequest(
//                payerAccount.getId(), "123456","1234", BigDecimal.valueOf(2000));
//
//        doReturn(payerAccount).when(transactionService).getAccountDataById(request.getPayerId());
//
//        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
//                () -> transactionService.transferBankAccount(request));
//        assertThat(exception.getMessage(), is("400 BAD_REQUEST \"Not enough balance to complete transfer.\""));
//        verifyNoInteractions(kafkaServiceClient);
//    }
//
//    @Test
//    @DisplayName("An exception should be thrown when a blocked account tries to transfer")
//    void transferExceptionWhenAccountIsBlocked() {
//        AccountData payerAccount = new AccountData(UUID.randomUUID(), BigDecimal.valueOf(10), true);
//        BranchAccountTransferRequest request = new BranchAccountTransferRequest(
//                payerAccount.getId(), "123456","1234", BigDecimal.valueOf(2000));
//
//        doReturn(payerAccount).when(transactionService).getAccountDataById(request.getPayerId());
//
//        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
//                () -> transactionService.transferBankAccount(request));
//        Assertions.assertEquals(exception.getMessage(), "400 BAD_REQUEST \"Account is blocked, cannot complete transfer.\"");
//        verifyNoInteractions(kafkaServiceClient);
//    }

//    @Test
//    @DisplayName("User should be able to get statement containing list of transactions")
//    void shouldGetStatementListContainingItems() {
//        AccountData account = new AccountData(UUID.randomUUID());
//
//        ArrayList<Transaction> expected = new ArrayList<>();
//        expected.add(new Transaction(1L, account.getId(), BigDecimal.valueOf(100), TransactionType.PAYMENT, UUID.randomUUID()));
//        expected.add(new Transaction(2L, account.getId(), BigDecimal.valueOf(200), TransactionType.PAYMENT, UUID.randomUUID()));
//        expected.add(new Transaction(3L, UUID.randomUUID(), BigDecimal.valueOf(300), TransactionType.INTRA_BANK_TRANSFER, account.getId()));
//        expected.add(new Transaction(4L, UUID.randomUUID(), BigDecimal.valueOf(400), TransactionType.INTRA_BANK_TRANSFER, account.getId()));
//
//        when(transactionRepository.findAllByPayerIdOrPayeeId(account.getId(), account.getId())).thenReturn(expected);
//
//        ArrayList<StatementTransactionResponse> transactions = new ArrayList<>();
//        for (Transaction transaction : expected) {
//            StatementTransactionResponse transactionDTO = new StatementTransactionResponse();
//            transactionDTO.setId(transaction.getId());
//            transactionDTO.setAmount(transaction.getAmount());
//            transactionDTO.setType(transaction.getType());
//
//            if(transaction.getPayerId().equals(account.getId())) {
//                transactionDTO.setAccountId(transaction.getPayerId());
//                transactionDTO.setDirection(TransactionDirection.SENT);
//            } else if(transaction.getPayeeId().equals(account.getId())) {
//                transactionDTO.setAccountId(transaction.getPayeeId());
//                transactionDTO.setDirection(TransactionDirection.RECEIVED);
//            }
//            transactions.add(transactionDTO);
//        }
//
//        StatementResponse response = transactionService.getStatement(account.getId());
//
//        Assertions.assertEquals(expected.size(), response.getTransactions().size());
//        verify(transactionRepository).findAllByPayerIdOrPayeeId(account.getId(), account.getId());
//        verifyNoMoreInteractions(transactionRepository);
//    }
}
