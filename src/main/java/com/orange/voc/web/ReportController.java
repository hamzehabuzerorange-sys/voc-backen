package com.orange.voc.web;

import com.orange.voc.repo.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin/reports")
public class ReportController {
    private final InvitationRepository invitations; private final ResponseRepository responses;
    public ReportController(InvitationRepository i, ResponseRepository r) { invitations = i; responses = r; }

    @GetMapping("/survey/{id}/summary")
    public Map<String, Object> summary(@PathVariable Long id) {
        Map<String, Long> byStatus = new LinkedHashMap<>(); long total = 0;
        for (Object[] row : invitations.countByStatus(id)) { byStatus.put((String) row[0], (Long) row[1]); total += (Long) row[1]; }
        long responded = responses.countBySurveyId(id);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("targeted", total); m.put("byStatus", byStatus); m.put("responded", responded); m.put("notResponded", total - byStatus.getOrDefault("COMPLETED", 0L));
        return m;
    }
    // TODO: detailed report (paged), customer-centric search, Excel (Apache POI) + PDF export.
}
