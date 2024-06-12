package com.viniciusm.mail_service.controller.request;

import lombok.Data;

@Data
public class SendEmailRequest {
    private String from;

    private String to;

    private String subject;

    private String body;

    private boolean isBodyHtml = false;
}
