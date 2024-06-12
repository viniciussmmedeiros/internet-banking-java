package com.internetbanking.accountapi.service;

import com.internetbanking.accountapi.controller.admin.request.AdminLoginRequest;
import com.internetbanking.accountapi.controller.admin.request.CreateAdminRequest;
import com.internetbanking.accountapi.controller.admin.response.AdminLoginResponse;
import com.internetbanking.accountapi.enums.AccountStatus;
import com.internetbanking.accountapi.model.Account;
import com.internetbanking.accountapi.model.Admin;
import com.internetbanking.accountapi.repository.AccountRepository;
import com.internetbanking.accountapi.repository.AdminRepository;
import com.internetbanking.accountapi.service.authApi.*;
import com.internetbanking.accountapi.service.email.EmailService;
import com.internetbanking.accountapi.service.email.SendEmailRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AuthService auth;

    @Value("${auth.appId}")
    private String appId;

    @Autowired
    private EmailService emailService;

    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found."));

        AuthLoginResponse loginResponse = null;

        try {
            loginResponse = auth.authLogin(new AuthLoginRequest(
                    appId,
                    request.getLogin(),
                    request.getPassword()
            ));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }

        return AdminLoginResponse.builder()
                .id(admin.getId().toString())
                .token(loginResponse.getToken())
                .date(loginResponse.getDate())
                .build();
    }

    public AuthRegisterResponse createAdmin(CreateAdminRequest request) {
        Admin admin = Admin.builder()
                .login(request.getLogin())
                .createdOn(LocalDateTime.now())
                .modifiedOn(LocalDateTime.now())
                .isActive(true)
                .financialInstitutionId(request.getFinancialInstitutionId())
                .build();

        AuthRegisterResponse authResponse = null;

        try {
            authResponse = auth.authRegister(new AuthRegisterRequest(
                    UUID.fromString(appId),
                    request.getLogin(),
                    request.getPassword(),
                    "ADMIN"
            ));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }

        adminRepository.save(admin);

        return authResponse;
    }

    public void blockAccount(UUID adminId, UUID accountId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        if(!account.getFinancialInstitutionId().equals(admin.getFinancialInstitutionId())) {
            throw new IllegalStateException("This account cannot be blocked by this admin.");
        }

        account.setBlocked(true);

        accountRepository.save(account);
    }

    public void unblockAccount(UUID adminId, UUID accountId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        if(!account.getFinancialInstitutionId().equals(admin.getFinancialInstitutionId())) {
            throw new IllegalStateException("This account cannot be unblocked by this admin.");
        }

        account.setBlocked(false);

        accountRepository.save(account);
    }

    @Transactional
    public void validateAccount(UUID adminId, UUID accountId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        if(account.getStatus() != AccountStatus.PENDING_ADMIN_VERIFICATION) {
            throw new IllegalStateException("Account is not in the state of admin verification");
        }

        if(!account.getFinancialInstitutionId().equals(admin.getFinancialInstitutionId())) {
            throw new IllegalStateException("This account cannot be validated by this admin.");
        }

        String accountNumber = String.valueOf((int) (Math.random() * Math.pow(10, 5)));

        boolean isAccountNumberPresent = accountRepository.findByBranchAndAccountNumberAndFinancialInstitutionId(account.getBranch(), accountNumber, account.getFinancialInstitutionId())
                .isPresent();

        if(isAccountNumberPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An unexpected error happened, try again later.");
        }

        account.setStatus(AccountStatus.VALID);
        account.setEnabled(true);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(accountNumber);

        accountRepository.save(account);

        // call auth-api to save user
        String login = accountNumber + "+" + account.getBranch();
        String password = String.valueOf((int) (Math.random() * Math.pow(10, 6)));

        auth.authRegister(new AuthRegisterRequest(
                UUID.fromString(appId),
                login,
                password,
                "COMMON"
        ));

        // send-email with confirmation -- body: login, password
        emailService.send(new SendEmailRequest(
                "noreply-internet-banking@ib.com",
                account.getEmail(),
                "Your account has been approved.",
                String.format("Your account has been approved by the admin! You can now use it.\nAccount number: %s\nBranch: %s\nPassword: %s", accountNumber, account.getBranch(), password)
        ));
    }

    public List<Account> getPendingValidationAccounts(UUID adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        return accountRepository.findAllByStatusAndFinancialInstitutionId(AccountStatus.PENDING_ADMIN_VERIFICATION, admin.getFinancialInstitutionId());
    }

    public Page<Account> listAccounts(UUID adminId, int page, int size) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        Pageable pageable = PageRequest.of(page, size);

        return accountRepository.findByFinancialInstitutionId(admin.getFinancialInstitutionId(), pageable);
    }
}
