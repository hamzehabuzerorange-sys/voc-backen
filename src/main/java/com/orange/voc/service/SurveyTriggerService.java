package com.orange.voc.service;

import com.orange.voc.domain.*;
import com.orange.voc.repo.ChannelRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** One entry point for: digital-channel survey request, external API trigger, and CTI/OST interaction processing. */
@Service
public class SurveyTriggerService {
    public static class Result { public final Survey survey; public final Invitation invitation; Result(Survey s, Invitation i) { survey = s; invitation = i; } }

    private final ChannelRepository channels; private final EligibilityService eligibility; private final SurveyService surveys; private final InvitationService invitations;
    public SurveyTriggerService(ChannelRepository c, EligibilityService e, SurveyService s, InvitationService i) { channels = c; eligibility = e; surveys = s; invitations = i; }

    @Transactional
    public Optional<Result> trigger(String msisdn, String channelCode, Long segmentId, Long interactionId, boolean dispatch) {
        Channel ch = channels.findByCodeAndActiveTrue(channelCode).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown channel"));
        if (!eligibility.isEligible(msisdn, ch.getId(), segmentId)) return Optional.empty();
        Optional<Survey> s = surveys.pickSurvey(ch.getId(), segmentId);
        if (!s.isPresent()) return Optional.empty();
        Invitation inv = invitations.create(s.get(), ch, msisdn, interactionId);
        if (dispatch) invitations.dispatch(inv, s.get());
        return Optional.of(new Result(s.get(), inv));
    }
}
