package com.internetbanking.accountapi.controller.admin;

import com.internetbanking.accountapi.controller.admin.request.AdminLoginRequest;
import com.internetbanking.accountapi.controller.admin.request.CreateAdminRequest;
import com.internetbanking.accountapi.controller.admin.response.AdminLoginResponse;
import com.internetbanking.accountapi.model.Account;
import com.internetbanking.accountapi.service.AdminService;
import com.internetbanking.accountapi.service.authApi.AuthRegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin Controller", description = "Handles admin operations such as registration, login, account blocking, and account validation.")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Logs into an admin account and returns the token.", method = "POST")
    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }

    @Operation(summary = "Creates a new admin account and returns a registration response.", method = "POST")
    @PostMapping("/create")
    public AuthRegisterResponse createAdmin(@Valid @RequestBody CreateAdminRequest request) {
            return adminService.createAdmin(request);
    }

    @Operation(summary = "Blocks an account identified by the given account ID.", method = "PATCH")
    @PatchMapping("/{adminId}/accounts/block/{accountId}")
    public void blockAccount(@PathVariable UUID adminId, @PathVariable UUID accountId) {
        adminService.blockAccount(adminId, accountId);
    }

    @Operation(summary = "Unblocks an account identified by the given account ID.", method = "PATCH")
    @PatchMapping("/{adminId}/accounts/unblock/{accountId}")
    public void unblockAccount(@PathVariable UUID adminId, @PathVariable UUID accountId) {
        adminService.unblockAccount(adminId, accountId);
    }

    @Operation(summary = "List accounts.", method = "GET")
    @GetMapping("/{adminId}/accounts/list")
    public Page<Account> listAccounts(@PathVariable UUID adminId,
              @RequestParam(value = "page", defaultValue = "0") int page,
              @RequestParam(value = "size", defaultValue = "10") int size) {
        return adminService.listAccounts(adminId, page, size);
    }

    @Operation(summary = "Gets a list of accounts pending validation for the specified admin's financial institution.", method = "GET")
    @GetMapping("/{adminId}/accounts/pending-validation/list")
    public List<Account> getPendingValidationAccounts(@PathVariable UUID adminId) {
        return adminService.getPendingValidationAccounts(adminId);
    }

    @Operation(summary = "Validates an account identified by the given account ID for the specified admin's financial institution.", method = "PATCH")
    @PatchMapping("/{adminId}/accounts/validate/{accountId}")
    public void validateAccount(@PathVariable UUID adminId, @PathVariable UUID accountId) {
        adminService.validateAccount(adminId, accountId);
    }
}
