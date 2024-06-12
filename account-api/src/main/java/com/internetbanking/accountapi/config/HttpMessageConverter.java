package com.internetbanking.accountapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        return MediaType.APPLICATION_OCTET_STREAM.includes(mediaType) && super.canRead(mediaType);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return MediaType.APPLICATION_OCTET_STREAM.includes(mediaType) && super.canWrite(mediaType);
    }
}