package com.internetbanking.accountapi.service;

import com.internetbanking.accountapi.controller.account.request.CompleteAccountCreationRequest;
import com.internetbanking.accountapi.controller.account.request.GetAccountDataRequest;
import com.internetbanking.accountapi.controller.account.request.InitiateAccountCreationRequest;
import com.internetbanking.accountapi.controller.account.request.LoginRequest;
import com.internetbanking.accountapi.controller.account.response.AccountData;
import com.internetbanking.accountapi.controller.account.response.AccountTransactionDataResponse;
import com.internetbanking.accountapi.controller.account.response.LoginResponse;
import com.internetbanking.accountapi.enums.AccountStatus;
import com.internetbanking.accountapi.enums.TransactionType;
import com.internetbanking.accountapi.enums.UpdateBalanceType;
import com.internetbanking.accountapi.kafka.BalanceUpdateRequest;
import com.internetbanking.accountapi.kafka.CompleteTransactionRequest;
import com.internetbanking.accountapi.kafka.KafkaClientService;
import com.internetbanking.accountapi.model.Account;
import com.internetbanking.accountapi.model.ConfirmationToken;
import com.internetbanking.accountapi.model.FinancialInstitution;
import com.internetbanking.accountapi.model.PixKey;
import com.internetbanking.accountapi.repository.AccountRepository;
import com.internetbanking.accountapi.repository.ConfirmationTokenRepository;
import com.internetbanking.accountapi.repository.FinancialInstitutionRepository;
import com.internetbanking.accountapi.repository.PixKeyRepository;
import com.internetbanking.accountapi.service.authApi.AuthLoginRequest;
import com.internetbanking.accountapi.service.authApi.AuthLoginResponse;
import com.internetbanking.accountapi.service.authApi.AuthService;
import com.internetbanking.accountapi.service.branch.Branch;
import com.internetbanking.accountapi.service.email.EmailService;
import com.internetbanking.accountapi.service.email.SendEmailRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Value("${confirmation-link.base.url}")
    private String confirmationLinkBaseUrl;

    @Autowired
    private AuthService auth;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenRepository tokenRepository;

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private FinancialInstitutionRepository financialInstitutionRepository;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByBranchAndAccountNumberAndFinancialInstitutionId(request.getBranch(), request.getAccountNumber(), request.getFinancialInstitutionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found."));

        if (account.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is blocked.");
        }

        String login = request.getAccountNumber() + "+" + request.getBranch();

        AuthLoginRequest authLoginRequest = new AuthLoginRequest(appId, login, request.getPassword());

        try {
            AuthLoginResponse authLoginResponse = auth.authLogin(authLoginRequest);

            return new LoginResponse(account.getId(), account.getFirstName(), authLoginResponse.getToken());
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    public AccountData getBalanceData(UUID accountId) {
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

        if(request.getAccountNumber() != null && request.getBranch() != null && request.getFinancialInstitutionId() != null) {
            account = accountRepository.findByBranchAndAccountNumberAndFinancialInstitutionId(request.getBranch(), request.getAccountNumber(), request.getFinancialInstitutionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
        }

        if(request.getPixKey() != null) {
            PixKey pixKey = pixKeyRepository.findByValue(request.getPixKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pix key not found."));

            account = accountRepository.findById(pixKey.getAccountId())
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
            if(request.getTransactionType() == TransactionType.BANK_TRANSFER) {
                accountData = this.handleBalanceUpdate(new BalanceUpdateRequest.Builder()
                                .accountNumber(request.getAccountNumber())
                                .branch(request.getBranch())
                                .financialInstitutionId(request.getFinancialInstitutionId())
                                .amount(request.getAmount())
                                .updateType(UpdateBalanceType.SUM)
                                .build());
            }

            if(request.getTransactionType() == TransactionType.PIX_TRANSFER) {
                PixKey pixKey = pixKeyRepository.findByValue(request.getPixKey())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pix key not found."));

                if(pixKey.isDeleted()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pix key is not enabled.");
                }

                accountData = this.handleBalanceUpdate(new BalanceUpdateRequest.Builder()
                        .pixKey(request.getPixKey())
                        .amount(request.getAmount())
                        .updateType(UpdateBalanceType.SUM)
                        .build());
            }

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

        completeTransactionRequest.setPayeeName("BILLET_PAYEE");

        Account payerAccount = accountRepository.findById(request.getPayerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        completeTransactionRequest.setPayerName(payerAccount.getFirstName());

        if(accountData != null) {
            Account payeeAccount = accountRepository.findById(accountData.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payee account not found."));
            completeTransactionRequest.setPayeeName(payeeAccount.getFirstName());
            completeTransactionRequest.setPayeeId(accountData.getId());
        }

        kafkaClientService.produceCompleteTransactionRequest(completeTransactionRequest);
    }

    public void initiateAccountCreation(InitiateAccountCreationRequest request) {
        Optional<Account> existingAccount = accountRepository.findByCpf(request.getCpf());

        if(existingAccount.isPresent()) {
            if (existingAccount.get().getStatus() == AccountStatus.PENDING_EMAIL_VERIFICATION) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing verification email.");
            }

            if (existingAccount.get().getStatus() == AccountStatus.PENDING_ADMIN_VERIFICATION) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already made, await for admin approval.");
            }

            if (existingAccount.get().getStatus() == AccountStatus.VALID) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already created.");
            }
        }

        financialInstitutionRepository.findById(request.getFinancialInstitutionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Financial institution not found or not supported."));

        Account newAccount = Account.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .status(AccountStatus.PENDING_EMAIL_VERIFICATION)
                .isEnabled(false)
                .financialInstitutionId(request.getFinancialInstitutionId())
                .build();

        accountRepository.save(newAccount);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .account(newAccount)
                .build();

        tokenRepository.save(confirmationToken);

        String confirmationLink = confirmationLinkBaseUrl + token;

        // send email with confirmation link.
        emailService.send(new SendEmailRequest(
                "noreply-internet-banking@ib.com",
                request.getEmail(),
                "Confirm your email",
                buildConfirmationEmail(request.getFirstName(), confirmationLink),
                true
        ));
    }

    public void completeAccountCreation(CompleteAccountCreationRequest request, MultipartFile selfieDocument, MultipartFile proofOfAddress) {
        Account account = accountRepository.findByCpf(request.getCpf())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        if (account.getStatus() != AccountStatus.PENDING_COMPLETION_DATA) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not in the state of missing completion data.");
        }

        try {
            account.setSelfieDocument(selfieDocument.getBytes());
            account.setProofOfAddress(proofOfAddress.getBytes());
        } catch (Exception exception) {
            throw new RuntimeException("An unexpected error occurred.");
        }

        // verify if the financial institution has the branch
        List<Branch> branches = financialInstitutionService.listInstitutionBranches(account.getFinancialInstitutionId());
        if(branches.stream().noneMatch(branch -> branch.getCodigoCompe().equals(request.getBranch()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your financial institution doesn't have the provided branch.");
        }

        account.setBranch(request.getBranch());
        account.setAddress(request.getAddress());
        account.setStatus(AccountStatus.PENDING_ADMIN_VERIFICATION);

        accountRepository.save(account);

        emailService.send(new SendEmailRequest("noreply-internet-banking@ib.com",
                account.getEmail(),
                "Completion data received.",
                "Your data has been received. An admin will analyze your data. Please await approval."));
    }

    @Transactional
    public String verifyEmail(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found."));

        if(confirmationToken.getConfirmedAt() != null) {{
            throw new IllegalStateException("Token already confirmed.");
        }}

        if(confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired.");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        tokenRepository.save(confirmationToken);

        UUID accountId = confirmationToken.getAccount().getId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Account not found"));

        account.setStatus(AccountStatus.PENDING_COMPLETION_DATA);
        accountRepository.save(account);

        emailService.send(new SendEmailRequest(
                "noreply-internet-banking@ib.com",
                account.getEmail(),
                "Email confirmed!.",
                "You can now proceed to send us the remaining documentation."));

        return "Email confirmed! You can now close this tab.";
    }

    private String buildConfirmationEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    public AccountTransactionDataResponse getAccountData(GetAccountDataRequest request) {
        Optional<Account> account = Optional.empty();

        if(request.getPixKey() != null) {
            PixKey pixKey = pixKeyRepository.findByValue(request.getPixKey())
                    .orElseThrow(() -> new IllegalStateException("Key not found."));

            account = accountRepository.findById(pixKey.getAccountId());
        }

        if(request.getFinancialInstitutionId() != null && request.getAccountNumber() != null && request.getBranch() != null) {
            account = accountRepository.findByFinancialInstitutionIdAndAccountNumberAndBranch(request.getFinancialInstitutionId(),
                    request.getAccountNumber(), request.getBranch());
        }

        if(account.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found.");
        }

        String cpf = account.get().getCpf();

        FinancialInstitution financialInstitution = financialInstitutionRepository.findById(account.get().getFinancialInstitutionId())
                .orElseThrow(() -> new IllegalStateException("Financial Institution not found."));

        return AccountTransactionDataResponse.builder()
                .fullName(String.format("%s %s", account.get().getFirstName(), account.get().getLastName()))
                .cpf(String.format("***.%s%s%s.%s%s%s-**", cpf.charAt(3), cpf.charAt(4), cpf.charAt(5), cpf.charAt(6), cpf.charAt(7), cpf.charAt(8)))
                .financialInstitution(financialInstitution.getName())
                .build();
    }
}
