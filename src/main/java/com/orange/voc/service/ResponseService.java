package com.orange.voc.service;

import com.orange.voc.domain.*;
import com.orange.voc.repo.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResponseService {
    public static class AnswerDto { public Long questionId; public Long optionId; public String value; }
    public static class SubmitRequest { public String token; public String language; public List<AnswerDto> answers = new ArrayList<>(); }

    private final InvitationRepository invitations; private final SurveyRepository surveys; private final ResponseRepository responses;
    public ResponseService(InvitationRepository i, SurveyRepository s, ResponseRepository r) { invitations = i; surveys = s; responses = r; }

    /** Used by the public page to load the survey; enforces the BLOCK expiry behaviour. */
    public Survey openSurvey(String token) {
        Invitation inv = find(token); checkOpen(inv);
        return surveys.findById(inv.getSurveyId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Long submit(String token, SubmitRequest req) {
        Invitation inv = find(token);
        if ("COMPLETED".equals(inv.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "already submitted");
        Survey s = surveys.findById(inv.getSurveyId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean expired = checkOpen(inv);

        Set<Long> answered = req.answers.stream().map(a -> a.questionId).collect(Collectors.toSet());
        for (Question q : s.getQuestions())
            if (Boolean.TRUE.equals(q.getMandatory()) && !"INFO".equals(q.getType()) && !answered.contains(q.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mandatory question " + q.getId() + " not answered");
        // NOTE: with branching, mandatory questions on skipped branches should be excluded - extend using Question.branchingJson.

        SurveyResponse r = new SurveyResponse();
        r.setInvitationId(inv.getId()); r.setSurveyId(s.getId()); r.setSurveyVersion(inv.getSurveyVersion()); r.setLanguage(req.language); r.setAfterExpiry(expired);
        for (AnswerDto a : req.answers) { ResponseAnswer ra = new ResponseAnswer(); ra.setQuestionId(a.questionId); ra.setOptionId(a.optionId); ra.setValueText(a.value); r.getAnswers().add(ra); }
        inv.setStatus("COMPLETED"); invitations.save(inv);
        return responses.save(r).getId();
    }

    private Invitation find(String token) { return invitations.findByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid link")); }

    /** @return true if the invitation is expired but late answers are allowed */
    private boolean checkOpen(Invitation inv) {
        boolean expired = inv.getExpiresAt() != null && LocalDateTime.now().isAfter(inv.getExpiresAt());
        if (!expired) return false;
        Survey s = surveys.findById(inv.getSurveyId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if ("BLOCK".equals(s.getExpiryBehavior())) throw new ResponseStatusException(HttpStatus.GONE, "survey expired");
        return true;
    }
}
