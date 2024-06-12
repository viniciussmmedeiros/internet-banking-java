package com.internetbanking.accountapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(title = "Account Management API", version = "1", description = "An API developed to handle bank account-related operations."))
@SpringBootApplication
@EnableFeignClients
public class AccountApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountApiApplication.class, args);
	}

}
