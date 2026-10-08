package com.orange.voc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingMessageGateway implements MessageGateway {
    private static final Logger log = LoggerFactory.getLogger(LoggingMessageGateway.class);
    @Override public boolean send(String method, String recipient, String text) {
        log.info("[{}] -> {} : {}", method, recipient, text);
        return true;
    }
}
