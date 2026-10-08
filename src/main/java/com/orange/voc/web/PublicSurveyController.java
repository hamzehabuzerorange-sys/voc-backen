package com.orange.voc.web;

import com.orange.voc.domain.Survey;
import com.orange.voc.service.ResponseService;
import java.util.*;
import org.springframework.web.bind.annotation.*;

/** Used by the customer-facing survey web page opened from the SMS / e-mail / WhatsApp link. */
@RestController @RequestMapping("/api/public/surveys")
public class PublicSurveyController {
    private final ResponseService responses;
    public PublicSurveyController(ResponseService r) { responses = r; }

    @GetMapping("/{token}") public Survey open(@PathVariable String token) { return responses.openSurvey(token); }

    @PostMapping("/{token}/responses")
    public Map<String, Object> submit(@PathVariable String token, @RequestBody ResponseService.SubmitRequest r) {
        return Collections.<String, Object>singletonMap("responseId", responses.submit(token, r));
    }
}
