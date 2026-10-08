package com.orange.voc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.voc.domain.*;
import com.orange.voc.repo.SurveyRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SurveyService {
    private final SurveyRepository repo; private final AuditService audit; private final JdbcTemplate jdbc; private final ObjectMapper mapper;
    public SurveyService(SurveyRepository r, AuditService a, JdbcTemplate j, ObjectMapper m) { repo = r; audit = a; jdbc = j; mapper = m; }

    public Survey get(Long id) { return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "survey")); }

    @Transactional public Survey create(Survey s, String user) {
        s.setId(null); s.setStatus(SurveyStatus.DRAFT); s.setCreatedBy(user); s.setVersion(1);
        validate(s);
        Survey saved = repo.save(s); audit.log("SURVEY_CREATE", "survey", saved.getId(), saved.getCode()); return saved;
    }

    @Transactional public Survey update(Long id, Survey in) {
        Survey cur = get(id);
        if (cur.getStatus() != SurveyStatus.DRAFT) throw new ResponseStatusException(HttpStatus.CONFLICT, "only DRAFT surveys can be edited");
        in.setId(id); in.setStatus(SurveyStatus.DRAFT); in.setCreatedBy(cur.getCreatedBy()); in.setVersion(cur.getVersion());
        validate(in);
        Survey saved = repo.save(in); audit.log("SURVEY_UPDATE", "survey", id, null); return saved;
    }

    @Transactional public Survey submitForApproval(Long id) { return move(id, SurveyStatus.DRAFT, SurveyStatus.PENDING_APPROVAL, null); }

    @Transactional public Survey approve(Long id, String approver) {
        Survey s = move(id, SurveyStatus.PENDING_APPROVAL, SurveyStatus.APPROVED, approver);
        s.setApprovedBy(approver);
        try { // immutable snapshot -> historical responses stay interpretable
            jdbc.update("INSERT INTO survey_snapshot(survey_id, version, snapshot_json) VALUES (?,?,?)", id, s.getVersion(), mapper.writeValueAsString(s));
        } catch (Exception e) { throw new IllegalStateException(e); }
        return s;
    }

    @Transactional public Survey activate(Long id) { return move(id, SurveyStatus.APPROVED, SurveyStatus.ACTIVE, null); }
    @Transactional public Survey deactivate(Long id) { return move(id, SurveyStatus.ACTIVE, SurveyStatus.INACTIVE, null); }

    /** Editing an approved survey = new version: copy back to DRAFT. */
    @Transactional public Survey newVersion(Long id) {
        Survey s = get(id);
        if (s.getStatus() == SurveyStatus.DRAFT || s.getStatus() == SurveyStatus.PENDING_APPROVAL) throw new ResponseStatusException(HttpStatus.CONFLICT, "already editable");
        s.setStatus(SurveyStatus.DRAFT); s.setVersion(s.getVersion() + 1); audit.log("SURVEY_NEW_VERSION", "survey", id, "v" + s.getVersion()); return s;
    }

    /** Copy questions + options from another survey (ids stripped). */
    @Transactional public Survey copyQuestions(Long targetId, Long fromId) {
        Survey target = get(targetId); Survey from = get(fromId);
        if (target.getStatus() != SurveyStatus.DRAFT) throw new ResponseStatusException(HttpStatus.CONFLICT, "target must be DRAFT");
        int pos = target.getQuestions().size();
        for (Question q : from.getQuestions()) {
            Question c = new Question();
            c.setPosition(++pos); c.setType(q.getType()); c.setMandatory(q.getMandatory()); c.setTextEn(q.getTextEn()); c.setTextAr(q.getTextAr()); c.setConfigJson(q.getConfigJson());
            for (AnswerOption o : q.getOptions()) { AnswerOption n = new AnswerOption(); n.setPosition(o.getPosition()); n.setTextEn(o.getTextEn()); n.setTextAr(o.getTextAr()); n.setValue(o.getValue()); n.setEmoji(o.getEmoji()); c.getOptions().add(n); }
            target.getQuestions().add(c); // branching is not copied: it references source question ids
        }
        return target;
    }

    /** Active, inside its start/end window, matching channel (+segment); falls back to the "all channels" default survey. */
    public Optional<Survey> pickSurvey(Long channelId, Long segmentId) {
        LocalDateTime now = LocalDateTime.now();
        List<Survey> hits = repo.findForChannel(channelId, SurveyStatus.ACTIVE).stream().filter(s -> inWindow(s, now))
                .filter(s -> s.getSegmentId() == null || s.getSegmentId().equals(segmentId))
                .sorted(Comparator.comparingInt((Survey s) -> s.getSegmentId() != null ? 0 : 1)).collect(Collectors.toList());
        if (!hits.isEmpty()) return Optional.of(hits.get(0));
        return repo.findByDefaultForAllChannelsTrueAndStatus(SurveyStatus.ACTIVE).stream().filter(s -> inWindow(s, now)).findFirst();
    }

    private boolean inWindow(Survey s, LocalDateTime now) {
        return (s.getStartAt() == null || !s.getStartAt().isAfter(now)) && (s.getEndAt() == null || s.getEndAt().isAfter(now));
    }

    private void validate(Survey s) {
        if (Boolean.TRUE.equals(s.getRequireMandatory()) && s.getQuestions().stream().noneMatch(q -> Boolean.TRUE.equals(q.getMandatory())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "survey must contain at least one mandatory question");
    }

    private Survey move(Long id, SurveyStatus from, SurveyStatus to, String detail) {
        Survey s = get(id);
        if (s.getStatus() != from) throw new ResponseStatusException(HttpStatus.CONFLICT, "expected status " + from + " but was " + s.getStatus());
        s.setStatus(to); audit.log("SURVEY_" + to, "survey", id, detail); return s;
    }
}
