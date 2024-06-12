package com.internetbanking.accountapi.service.email;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service", url = "${mail.service.url}/mail")
public interface EmailService {

    @PostMapping(value = "/send")
    void send(@RequestBody SendEmailRequest request);
}
