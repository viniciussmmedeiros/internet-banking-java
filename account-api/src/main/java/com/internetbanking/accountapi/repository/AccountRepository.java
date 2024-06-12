package com.internetbanking.accountapi.repository;

import com.internetbanking.accountapi.enums.AccountStatus;
import com.internetbanking.accountapi.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByCpf(String cpf);

    List<Account> findAllByStatusAndFinancialInstitutionId(AccountStatus accountStatus, Long financialInstitutionId);

    Page<Account> findByFinancialInstitutionId(Long financialInstitutionId, Pageable pageable);

    Optional<Account> findByFinancialInstitutionIdAndAccountNumberAndBranch(Long financialInstitutionId, String accountNumber, String branch);

    Optional<Account> findByBranchAndAccountNumberAndFinancialInstitutionId(String branch, String accountNumber, Long financialInstitutionId);
}
