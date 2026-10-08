package com.orange.voc.domain;
import javax.persistence.*;

@Entity
public class FrequencyRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name; private Integer days;
    private String scope = "ALL_CHANNELS"; // ALL_CHANNELS | SAME_CHANNEL
    private Boolean enabled = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
