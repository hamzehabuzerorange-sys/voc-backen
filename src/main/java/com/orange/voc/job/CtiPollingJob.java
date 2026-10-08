package com.orange.voc.job;

import com.orange.voc.service.SurveyTriggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Polls the read-only CTI DB view. To finish:
 *  1. add a second DataSource (spring.cti-datasource.*) + JdbcTemplate for the CTI view
 *  2. SELECT rows newer than the last processed external_id
 *  3. INSERT IGNORE into `interaction` (unique source+external_id) and map team/segment (Fixed/Mobile, in-house/outsourced)
 *  4. call trigger.trigger(msisdn, channelCode, segmentId, interactionId, true)
 * The OST (shop) poller is identical with source='OST' and request_type mapping.
 */
@Component
public class CtiPollingJob {
    private static final Logger log = LoggerFactory.getLogger(CtiPollingJob.class);
    private final JobGate gate; private final SurveyTriggerService trigger;
    public CtiPollingJob(JobGate g, SurveyTriggerService t) { gate = g; trigger = t; }

    @Scheduled(fixedDelayString = "${voc.cti.poll-ms}")
    public void poll() {
        if (!gate.enabled("CTI_POLL")) return;
        log.debug("CTI poll tick (not yet implemented)");
        gate.ran("CTI_POLL", "OK");
    }
}
