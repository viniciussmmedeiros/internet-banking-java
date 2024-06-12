package com.internetbanking.accountapi.service;

import com.internetbanking.accountapi.controller.pix.request.CreateKeyRequest;
import com.internetbanking.accountapi.enums.PixKeyType;
import com.internetbanking.accountapi.model.Account;
import com.internetbanking.accountapi.model.PixKey;
import com.internetbanking.accountapi.repository.AccountRepository;
import com.internetbanking.accountapi.repository.PixKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixService {

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private AccountRepository accountRepository;

    public void createKey(CreateKeyRequest request) {
        Optional<PixKey> existingKey = pixKeyRepository.findByAccountIdAndType(request.getAccountId(), request.getType());

        if(existingKey.isPresent() && !existingKey.get().isDeleted()) {
            throw new IllegalStateException("Account already has key of type provided.");
        }

        // if type is cpf and is present, it means the key exists and is deleted
        if(request.getType() == PixKeyType.CPF && existingKey.isPresent()) {
            existingKey.get().setDeleted(false);
            existingKey.get().setModifiedOn(LocalDateTime.now());

            pixKeyRepository.save(existingKey.get());
            return;
        }

        String keyValue = "";

        if(request.getType() == PixKeyType.RANDOM) {
            keyValue = UUID.randomUUID().toString();
        }

        if(request.getType() == PixKeyType.EMAIL || request.getType() == PixKeyType.PHONE) {
            if(request.getValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key value was not provided.");
            }

            keyValue = request.getValue();
        }

        if(request.getType() == PixKeyType.CPF) {
            Account account = accountRepository.findById(request.getAccountId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
            keyValue = account.getCpf();
        }

        PixKey newKey = PixKey.builder()
                .accountId(request.getAccountId())
                .type(request.getType())
                .value(keyValue)
                .createdOn(LocalDateTime.now())
                .modifiedOn(LocalDateTime.now())
                .build();

        pixKeyRepository.save(newKey);
    }

    public void deleteKey(UUID accountId, UUID keyId) {
        PixKey pixKey = pixKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Key not found."));

        if(!pixKey.getAccountId().equals(accountId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not own this key.");
        }

        pixKey.setDeleted(true);
        pixKey.setModifiedOn(LocalDateTime.now());

        pixKeyRepository.save(pixKey);
    }

    public List<PixKey> listKeys(UUID accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));

        return pixKeyRepository.findAllByAccountId(accountId);
    }
}
