package com.internetbanking.accountapi.repository;

import com.internetbanking.accountapi.enums.PixKeyType;
import com.internetbanking.accountapi.model.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PixKeyRepository extends JpaRepository<PixKey, UUID> {
    Optional<PixKey> findByAccountIdAndType(UUID accountId, PixKeyType type);

    Optional<PixKey> findByValue(String value);

    List<PixKey> findAllByAccountId(UUID accountId);
}
