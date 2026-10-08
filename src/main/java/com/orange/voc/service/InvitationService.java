package com.orange.voc.service;

import com.orange.voc.domain.*;
import com.orange.voc.repo.InvitationRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {
    private static final String ALPHABET = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    private final SecureRandom rnd = new SecureRandom();
    private final InvitationRepository repo; private final MessageGateway gateway;
    @Value("${voc.public-base-url}") private String baseUrl;
    public InvitationService(InvitationRepository r, MessageGateway g) { repo = r; gateway = g; }

    public Invitation create(Survey s, Channel ch, String msisdn, Long interactionId) {
        Invitation i = new Invitation();
        i.setToken(newToken()); i.setSurveyId(s.getId()); i.setSurveyVersion(s.getVersion()); i.setChannelId(ch.getId());
        i.setInteractionId(interactionId); i.setMsisdn(msisdn); i.setDeliveryMethod(ch.getDeliveryMethod());
        if (s.getValidityDays() != null) i.setExpiresAt(LocalDateTime.now().plusDays(s.getValidityDays()));
        return repo.save(i);
    }

    public String link(Invitation i) { return baseUrl + "/s/" + i.getToken(); } // 10-char token => short link

    public void dispatch(Invitation i, Survey s) {
        if ("API".equals(i.getDeliveryMethod())) return; // digital channel renders the survey itself
        String text = welcome(s) + " " + link(i);
        boolean ok = gateway.send(i.getDeliveryMethod(), i.getMsisdn(), text);
        i.setStatus(ok ? "SENT" : "FAILED"); i.setSentAt(LocalDateTime.now()); repo.save(i);
    }

    private String welcome(Survey s) { return "Orange would love your feedback:"; /* TODO: load welcome_message by s.getWelcomeMessageId() */ }

    private String newToken() {
        String t;
        do { StringBuilder b = new StringBuilder(10); for (int k = 0; k < 10; k++) b.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length()))); t = b.toString(); }
        while (repo.findByToken(t).isPresent());
        return t;
    }
}
