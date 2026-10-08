package com.orange.voc.web;

import com.orange.voc.domain.*;
import com.orange.voc.repo.ChannelRepository;
import com.orange.voc.repo.SurveyRepository;
import com.orange.voc.service.InvitationService;
import com.orange.voc.service.SurveyService;
import java.security.Principal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin/surveys")
public class SurveyAdminController {
    private final SurveyService svc; private final SurveyRepository repo; private final ChannelRepository channels; private final InvitationService invitations;
    public SurveyAdminController(SurveyService s, SurveyRepository r, ChannelRepository c, InvitationService i) { svc = s; repo = r; channels = c; invitations = i; }

    @GetMapping public List<Survey> list() { return repo.findAll(); }
    @GetMapping("/{id}") public Survey get(@PathVariable Long id) { return svc.get(id); }

    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
    public Survey create(@RequestBody Survey s, Principal p) { return svc.create(s, p.getName()); }

    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
    public Survey update(@PathVariable Long id, @RequestBody Survey s) { return svc.update(id, s); }

    @PostMapping("/{id}/submit") @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
    public Survey submit(@PathVariable Long id) { return svc.submitForApproval(id); }

    @PostMapping("/{id}/approve") @PreAuthorize("hasAnyRole('ADMIN','APPROVER')")
    public Survey approve(@PathVariable Long id, Principal p) { return svc.approve(id, p.getName()); }

    @PostMapping("/{id}/activate") @PreAuthorize("hasAnyRole('ADMIN','APPROVER')")
    public Survey activate(@PathVariable Long id) { return svc.activate(id); }

    @PostMapping("/{id}/deactivate") @PreAuthorize("hasAnyRole('ADMIN','APPROVER')")
    public Survey deactivate(@PathVariable Long id) { return svc.deactivate(id); }

    @PostMapping("/{id}/new-version") @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
    public Survey newVersion(@PathVariable Long id) { return svc.newVersion(id); }

    @PostMapping("/{id}/copy-questions") @PreAuthorize("hasAnyRole('ADMIN','DESIGNER')")
    public Survey copy(@PathVariable Long id, @RequestParam Long fromSurveyId) { return svc.copyQuestions(id, fromSurveyId); }

    /** Preview/test at any status (even DRAFT): creates a throw-away invitation (msisdn "TEST") and returns its token. Nothing is sent. */
    @PostMapping("/{id}/test-link") @PreAuthorize("hasAnyRole('ADMIN','DESIGNER','APPROVER')")
    public Map<String, String> testLink(@PathVariable Long id) {
        Survey s = svc.get(id);
        Channel ch = s.getChannels().stream().findFirst().orElseGet(() -> channels.findAll().stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create a channel first")));
        Invitation inv = invitations.create(s, ch, "TEST", null);
        Map<String, String> m = new LinkedHashMap<>(); m.put("token", inv.getToken()); m.put("link", invitations.link(inv)); return m;
    }
}
