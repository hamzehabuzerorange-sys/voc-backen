package com.orange.voc.service;

/** Plug the real SMS / Email / WhatsApp provider integrations in here (one impl per method or a router). */
public interface MessageGateway {
    /** @return true when the provider accepted the message */
    boolean send(String method, String recipient, String text);
}
