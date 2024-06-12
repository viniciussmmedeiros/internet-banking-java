package com.internetbanking.accountapi.model;

import com.internetbanking.accountapi.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"financialInstitutionId", "branch", "accountNumber"})
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cpf;

    private BigDecimal balance = BigDecimal.ZERO;

    private String branch;

    private String accountNumber;

    private boolean isBlocked = false;

    private boolean isEnabled;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Embedded
    private Address address;

    private byte[] selfieDocument;
    private byte[] proofOfAddress;

    private Long financialInstitutionId;
}
