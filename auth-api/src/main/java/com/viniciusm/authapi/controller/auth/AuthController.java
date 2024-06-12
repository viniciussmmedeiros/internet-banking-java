package com.viniciusm.authapi.controller.auth;

import com.viniciusm.authapi.controller.auth.request.LoginRequest;
import com.viniciusm.authapi.controller.auth.request.RegisterRequest;
import com.viniciusm.authapi.controller.auth.response.LoginResponse;
import com.viniciusm.authapi.controller.auth.response.RegisterResponse;
import com.viniciusm.authapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Controller", description = "Handles authentication operations such as registration and login.")
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Registers a new user and returns a token.", method = "POST")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Logs in a user and returns a token.", method = "POST")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
