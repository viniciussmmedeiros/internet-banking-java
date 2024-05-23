package com.viniciusm.authapi.repository;

import com.viniciusm.authapi.controller.auth.request.RegisterRequest;
import com.viniciusm.authapi.enums.UserRole;
import com.viniciusm.authapi.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuthRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AuthRepository authRepository;

    @Test
    @DisplayName("Should fetch user correctly if registered")
    void findUserByLoginAndAppId() {
        RegisterRequest request = new RegisterRequest();
        request.setAppId(UUID.randomUUID());
        request.setLogin("login");
        request.setRole(UserRole.ADMIN);
        request.setPassword("password");

        createUser(request);
        Optional<User> result = authRepository.findByLoginAndAppId(request.getLogin(), request.getAppId());

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not find user when not registered")
    public void shouldNotFindUserByLoginAndAppId() {
        Optional<User> user = authRepository.findByLoginAndAppId("NonExistentLogin", UUID.randomUUID());

        assertThat(user.isEmpty()).isTrue();
    }

    private void createUser(RegisterRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setAppId(request.getAppId());
        user.setRole(request.getRole());

        entityManager.persist(user);
    }
}