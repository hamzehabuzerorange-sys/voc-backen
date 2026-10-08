package com.orange.voc.service;

import com.orange.voc.domain.*;
import com.orange.voc.repo.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

/** Frequency (contact-rule) + sampling checks. Disabled rules are simply not loaded. */
@Service
public class EligibilityService {
    private final FrequencyRuleRepository freq; private final SamplingRuleRepository sampling; private final InvitationRepository invitations;
    public EligibilityService(FrequencyRuleRepository f, SamplingRuleRepository s, InvitationRepository i) { freq = f; sampling = s; invitations = i; }

    public boolean isEligible(String msisdn, Long channelId, Long segmentId) {
        LocalDateTime now = LocalDateTime.now();
        for (FrequencyRule r : freq.findByEnabledTrue()) {
            LocalDateTime since = now.minusDays(r.getDays());
            boolean recent = "SAME_CHANNEL".equals(r.getScope())
                    ? invitations.existsByMsisdnAndChannelIdAndCreatedAtAfter(msisdn, channelId, since)
                    : invitations.existsByMsisdnAndCreatedAtAfter(msisdn, since);
            if (recent) return false;
        }
        return passesSampling(channelId, segmentId);
    }

    private boolean passesSampling(Long channelId, Long segmentId) {
        Optional<SamplingRule> rule = sampling.findByEnabledTrueAndChannelId(channelId).stream()
                .filter(r -> r.getSegmentId() == null || r.getSegmentId().equals(segmentId))
                .max(Comparator.comparingInt(r -> r.getSegmentId() != null ? 1 : 0)); // most specific wins
        // no enabled rule == sampling disabled == everybody eligible
        return !rule.isPresent() || ThreadLocalRandom.current().nextDouble() * 100 < rule.get().getPercentage();
    }
}
