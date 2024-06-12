package com.internetbanking.accountapi.service.authApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {

    @Value("${auth.url}")
    private String authUrl;

    public AuthLoginResponse authLogin(AuthLoginRequest request) {
        String url = authUrl + "/auth/login";

        return WebClient.create()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(AuthLoginResponse.class)
                .block();
    }

    public AuthRegisterResponse authRegister(AuthRegisterRequest request) {
        String url = authUrl + "/auth/register";

        return WebClient.create()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(AuthRegisterResponse.class)
                .block();
    }
}
