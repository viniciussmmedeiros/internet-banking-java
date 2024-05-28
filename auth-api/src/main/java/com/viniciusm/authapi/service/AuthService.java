package com.viniciusm.authapi.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.viniciusm.authapi.controller.auth.request.LoginRequest;
import com.viniciusm.authapi.controller.auth.request.RegisterRequest;
import com.viniciusm.authapi.controller.auth.response.LoginResponse;
import com.viniciusm.authapi.controller.auth.response.RegisterResponse;
import com.viniciusm.authapi.model.User;
import com.viniciusm.authapi.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public RegisterResponse register(RegisterRequest request) {
        Optional<User> existingUser = authRepository.findByLoginAndAppId(request.getLogin(), request.getAppId());

        if(existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Registration failed.");
        }

        User user = new User.Builder()
                .id(UUID.randomUUID())
                .login(request.getLogin())
                .role(request.getRole())
                .appId(request.getAppId())
                .build();

        String encodedPassword = new BCryptPasswordEncoder().encode(request.getPassword());
        user.setPassword(encodedPassword);

        User savedUser = authRepository.save(user);

        String token = generateToken(savedUser);

       return new RegisterResponse(savedUser, token);
    }

    public LoginResponse login(LoginRequest request) {
        User user = authRepository.findByLoginAndAppId(request.getLogin(), request.getAppId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password.");
        }

        String token = generateToken(user);

        return new LoginResponse(token, LocalDateTime.now());
    }

    private String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC512("auth-api-secret-key");
            return JWT.create()
                    .withSubject(user.getLogin())
                    .withClaim("role", user.getRole().toString())
                    .withIssuer("auth-api")
                    .withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Error generating JWT token.");
        }
    }
}
