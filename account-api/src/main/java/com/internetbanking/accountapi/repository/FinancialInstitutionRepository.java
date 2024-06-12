package com.internetbanking.accountapi.repository;

import com.internetbanking.accountapi.model.FinancialInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialInstitutionRepository extends JpaRepository<FinancialInstitution, Long> {
}
