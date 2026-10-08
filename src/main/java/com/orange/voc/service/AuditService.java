package com.orange.voc.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final JdbcTemplate jdbc;
    public AuditService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void log(String action, String entity, Object entityId, String detail) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String actor = a == null ? "system" : a.getName();
        jdbc.update("INSERT INTO audit_log(actor, action, entity, entity_id, detail) VALUES (?,?,?,?,?)",
                actor, action, entity, String.valueOf(entityId), detail);
    }
}
