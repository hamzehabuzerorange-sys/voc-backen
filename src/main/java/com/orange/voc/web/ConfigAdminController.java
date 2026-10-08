package com.orange.voc.web;

import com.orange.voc.domain.*;
import com.orange.voc.repo.*;
import com.orange.voc.security.Keys;
import com.orange.voc.service.AuditService;
import java.security.SecureRandom;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Channels, sampling/frequency rules, API keys, scheduled jobs. */
@RestController @RequestMapping("/api/admin") @PreAuthorize("hasRole('ADMIN')")
public class ConfigAdminController {
    private final ChannelRepository channels; private final FrequencyRuleRepository freq; private final SamplingRuleRepository sampling;
    private final ApiKeyRepository keys; private final JdbcTemplate jdbc; private final AuditService audit;
    public ConfigAdminController(ChannelRepository c, FrequencyRuleRepository f, SamplingRuleRepository s, ApiKeyRepository k, JdbcTemplate j, AuditService a) { channels = c; freq = f; sampling = s; keys = k; jdbc = j; audit = a; }

    @GetMapping("/channels") @PreAuthorize("isAuthenticated()") public List<Channel> channels() { return channels.findAll(); }
    @PostMapping("/channels") public Channel saveChannel(@RequestBody Channel c) { audit.log("CHANNEL_SAVE", "channel", c.getCode(), null); return channels.save(c); }

    @GetMapping("/frequency-rules") public List<FrequencyRule> freqRules() { return freq.findAll(); }
    @PostMapping("/frequency-rules") public FrequencyRule saveFreq(@RequestBody FrequencyRule r) { audit.log("FREQ_RULE_SAVE", "frequency_rule", r.getId(), r.getName()); return freq.save(r); }

    @GetMapping("/sampling-rules") public List<SamplingRule> samplingRules() { return sampling.findAll(); }
    @PostMapping("/sampling-rules") public SamplingRule saveSampling(@RequestBody SamplingRule r) { audit.log("SAMPLING_RULE_SAVE", "sampling_rule", r.getId(), null); return sampling.save(r); }

    /** The raw key is returned exactly once; only its SHA-256 is stored. */
    @PostMapping("/api-keys")
    public Map<String, String> createKey(@RequestParam String name) {
        byte[] b = new byte[32]; new SecureRandom().nextBytes(b);
        StringBuilder raw = new StringBuilder("voc_"); for (byte x : b) raw.append(String.format("%02x", x));
        ApiKey k = new ApiKey(); k.setName(name); k.setKeyHash(Keys.sha256(raw.toString())); keys.save(k);
        audit.log("API_KEY_CREATE", "api_key", k.getId(), name);
        Map<String, String> m = new LinkedHashMap<>(); m.put("name", name); m.put("apiKey", raw.toString()); return m;
    }

    @GetMapping("/jobs") public List<Map<String, Object>> jobs() { return jdbc.queryForList("SELECT * FROM scheduled_job"); }
    @PostMapping("/jobs/{name}/enabled") public void toggleJob(@PathVariable String name, @RequestParam boolean value) {
        jdbc.update("UPDATE scheduled_job SET enabled=? WHERE name=?", value, name); audit.log("JOB_TOGGLE", "job", name, String.valueOf(value));
    }

    @GetMapping("/audit-logs") public List<Map<String, Object>> auditLogs(@RequestParam(defaultValue = "100") int limit) {
        return jdbc.queryForList("SELECT * FROM audit_log ORDER BY id DESC LIMIT ?", Math.min(limit, 1000));
    }
}
