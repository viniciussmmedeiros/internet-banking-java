package com.internetbanking.accountapi.repository;

import com.internetbanking.accountapi.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByBranchAndAccountNumber(String branch, String accountNumber);
}
