package com.viniciusm.authapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viniciusm.authapi.controller.auth.request.LoginRequest;
import com.viniciusm.authapi.controller.auth.request.RegisterRequest;
import com.viniciusm.authapi.enums.UserRole;
import com.viniciusm.authapi.model.User;
import com.viniciusm.authapi.repository.AuthRepository;
import com.viniciusm.authapi.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    @Mock
    private AuthService authService;

    @Test
    @DisplayName("Endpoint should handle registration correctly if the data provided is right")
    public void testRegisterEndpoint() throws Exception {
        RegisterRequest request = new RegisterRequest(
                UUID.randomUUID(),
                "1234+87623-3",
                "password",
                UserRole.COMMON
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Endpoint should handle login correctly if the user exists")
    public void testLoginEndpoint() throws Exception {
        UUID appId = UUID.randomUUID();
        RegisterRequest registerRequest = new RegisterRequest(
                appId,
                "4321+5502-2",
                "password",
                UserRole.COMMON
        );

        createUser(registerRequest);

        LoginRequest request = new LoginRequest(
                appId,
                "4321+5502-2",
                "password"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    private void createUser(RegisterRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(request.getLogin());

        String encodedPassword = new BCryptPasswordEncoder().encode(request.getPassword());
        user.setPassword(encodedPassword);

        user.setAppId(request.getAppId());
        user.setRole(request.getRole());

        authRepository.save(user);
    }
}
