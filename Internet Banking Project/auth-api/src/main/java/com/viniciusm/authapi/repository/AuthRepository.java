package com.viniciusm.authapi.repository;

import com.viniciusm.authapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLoginAndAppId(String login, UUID appId);
}
