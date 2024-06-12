package com.viniciusm.mail_service.controller;

import com.viniciusm.mail_service.controller.request.SendEmailRequest;
import com.viniciusm.mail_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class EmailServiceController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public void sendEmail(@RequestBody SendEmailRequest request) {
            emailService.send(request);
    }
}
