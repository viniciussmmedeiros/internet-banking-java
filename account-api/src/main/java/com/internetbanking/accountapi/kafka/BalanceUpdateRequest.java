package com.internetbanking.accountapi.kafka;

import com.internetbanking.accountapi.enums.TransactionType;
import com.internetbanking.accountapi.enums.UpdateBalanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequest {

    private UUID payerId;

    private String accountNumber;

    private String branch;

    private String pixKey;

    private BigDecimal amount;

    private UpdateBalanceType updateType;

    private TransactionType transactionType;

    private Long financialInstitutionId;

    private BalanceUpdateRequest(Builder builder) {
        this.payerId = builder.payerId;
        this.accountNumber = builder.accountNumber;
        this.branch = builder.branch;
        this.amount = builder.amount;
        this.pixKey = builder.pixKey;
        this.updateType = builder.updateType;
        this.transactionType = builder.transactionType;
        this.financialInstitutionId = builder.financialInstitutionId;
    }

    public static class Builder {
        private UUID payerId;

        private String accountNumber;

        private String branch;

        private String pixKey;

        private BigDecimal amount;

        private UpdateBalanceType updateType;

        private TransactionType transactionType;

        private Long financialInstitutionId;

        public Builder payerId(UUID payerId) {
            this.payerId = payerId;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder pixKey(String pixKey) {
            this.pixKey = pixKey;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder updateType(UpdateBalanceType updateType) {
            this.updateType = updateType;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder financialInstitutionId(Long financialInstitutionId) {
            this.financialInstitutionId = financialInstitutionId;
            return this;
        }

        public BalanceUpdateRequest build() {
            return new BalanceUpdateRequest(this);
        }
    }
}
