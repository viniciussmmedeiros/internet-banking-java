package com.viniciusm.authapi.service;

import com.viniciusm.authapi.controller.auth.request.LoginRequest;
import com.viniciusm.authapi.controller.auth.request.RegisterRequest;
import com.viniciusm.authapi.controller.auth.response.LoginResponse;
import com.viniciusm.authapi.controller.auth.response.RegisterResponse;
import com.viniciusm.authapi.enums.UserRole;
import com.viniciusm.authapi.model.User;
import com.viniciusm.authapi.repository.AuthRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class AuthServiceTest {
    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegister() {
        RegisterRequest request = new RegisterRequest(UUID.randomUUID(), "login", "password", UserRole.COMMON);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setAppId(request.getAppId());
        user.setRole(request.getRole());

        when(authRepository.save(any())).thenReturn(user);

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(response.getLogin(), request.getLogin());
        assertEquals(response.getPassword(), request.getPassword());
        assertEquals(response.getRole(), request.getRole());
        assertNotNull(response.getToken());
    }

    @Test
    @DisplayName("User should be able to login successfully")
    void testLogin() {
        LoginRequest request = new LoginRequest(UUID.randomUUID(), "login", "password");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(request.getLogin());
        user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        user.setRole(UserRole.COMMON);
        user.setAppId(UUID.randomUUID());

        when(authRepository.findByLoginAndAppId(request.getLogin(), request.getAppId())).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
    }

    @Test
    @DisplayName("Should throw not found exception if user is not registered")
    void testLoginNotFoundException() {
        LoginRequest request = new LoginRequest(UUID.randomUUID(), "login", "password");

        when(authRepository.findByLoginAndAppId(request.getLogin(), request.getAppId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResponseStatusException.class, () -> authService.login(request));
        Assertions.assertEquals("404 NOT_FOUND \"User not found.\"", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw bad request exception if given password doesn't match")
    void testLoginWrongPasswordException() {
        LoginRequest request = new LoginRequest(UUID.randomUUID(), "login", "password");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(request.getLogin());
        user.setAppId(UUID.randomUUID());
        user.setRole(UserRole.COMMON);
        user.setPassword(new BCryptPasswordEncoder().encode("1234"));

        when(authRepository.findByLoginAndAppId(request.getLogin(), request.getAppId())).thenReturn(java.util.Optional.of(user));

        Exception exception = assertThrows(ResponseStatusException.class, () -> authService.login(request));
        Assertions.assertEquals("400 BAD_REQUEST \"Wrong password.\"", exception.getMessage());
    }
}