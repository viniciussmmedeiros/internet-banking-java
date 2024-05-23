package com.internetbanking.transactionapi.model;

import com.internetbanking.transactionapi.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID payerId;

    private BigDecimal amount;

    private TransactionType type;

    private UUID payeeId;

    public Transaction(UUID payerId, BigDecimal amount, TransactionType type) {
        this.payerId = payerId;
        this.amount = amount;
        this.type = type;
    }

    public Transaction(UUID payerId, BigDecimal amount, TransactionType type, UUID payeeId) {
        this.payerId = payerId;
        this.amount = amount;
        this.type = type;
        this.payeeId = payeeId;
    }
}
