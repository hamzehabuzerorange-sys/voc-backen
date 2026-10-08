package com.orange.voc.job;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Surveys whose end date has passed become INACTIVE automatically. */
@Component
public class SurveyExpiryJob {
    private final JdbcTemplate jdbc; private final JobGate gate;
    public SurveyExpiryJob(JdbcTemplate j, JobGate g) { jdbc = j; gate = g; }

    @Scheduled(fixedDelay = 60000)
    public void run() {
        if (!gate.enabled("SURVEY_EXPIRY")) return;
        jdbc.update("UPDATE survey SET status='INACTIVE' WHERE status='ACTIVE' AND end_at IS NOT NULL AND end_at <= NOW()");
        gate.ran("SURVEY_EXPIRY", "OK");
    }
}
