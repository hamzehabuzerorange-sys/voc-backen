package com.orange.voc.web;

import com.orange.voc.service.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;

/** Server-to-server API (X-API-Key). The calling channel renders the survey itself. */
@RestController @RequestMapping("/api/v1")
public class DigitalChannelController {
    public static class TriggerRequest { public String msisdn; public String channelCode; public Long segmentId; }

    private final SurveyTriggerService trigger; private final ResponseService responses;
    public DigitalChannelController(SurveyTriggerService t, ResponseService r) { trigger = t; responses = r; }

    /** Channel asks: "is there a survey for this MSISDN?" -> {available:false} or survey + token. No message is sent. */
    @PostMapping("/digital/survey-requests")
    public Map<String, Object> request(@RequestBody TriggerRequest r) {
        return trigger.trigger(r.msisdn, r.channelCode, r.segmentId, null, false).map(res -> {
            Map<String, Object> m = new LinkedHashMap<>(); m.put("available", true); m.put("token", res.invitation.getToken()); m.put("survey", res.survey); return m;
        }).orElseGet(() -> Collections.<String, Object>singletonMap("available", false));
    }

    /** External system triggers a survey; platform evaluates rules and distributes via the channel's method. */
    @PostMapping("/triggers")
    public Map<String, Object> fire(@RequestBody TriggerRequest r) {
        return trigger.trigger(r.msisdn, r.channelCode, r.segmentId, null, true).map(res -> {
            Map<String, Object> m = new LinkedHashMap<>(); m.put("triggered", true); m.put("token", res.invitation.getToken()); return m;
        }).orElseGet(() -> Collections.<String, Object>singletonMap("triggered", false));
    }

    @PostMapping("/digital/responses")
    public Map<String, Object> submit(@RequestBody ResponseService.SubmitRequest r) {
        return Collections.<String, Object>singletonMap("responseId", responses.submit(r.token, r));
    }
}
