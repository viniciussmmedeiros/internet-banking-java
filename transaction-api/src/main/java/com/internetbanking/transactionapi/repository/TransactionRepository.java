package com.internetbanking.transactionapi.repository;

import com.internetbanking.transactionapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    ArrayList<Transaction> findAllByPayerIdOrPayeeId(UUID accountId, UUID accountId1);
}
