package com.orange.voc.job;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Lets administrators enable/disable background jobs from the UI (scheduled_job table). */
@Component
public class JobGate {
    private final JdbcTemplate jdbc;
    public JobGate(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public boolean enabled(String name) {
        Boolean b = jdbc.queryForObject("SELECT enabled FROM scheduled_job WHERE name=?", Boolean.class, name); return Boolean.TRUE.equals(b);
    }
    public void ran(String name, String status) { jdbc.update("UPDATE scheduled_job SET last_run_at=NOW(), last_status=? WHERE name=?", status, name); }
}
