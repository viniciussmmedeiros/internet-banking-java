package com.internetbanking.accountapi.service.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendEmailRequest {
    private String from;

    private String to;

    private String subject;

    private String body;

    private boolean isBodyHtml;

    public SendEmailRequest(String from, String to, String subject, String body) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}