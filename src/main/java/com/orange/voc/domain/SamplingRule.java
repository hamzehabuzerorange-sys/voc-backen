package com.orange.voc.domain;
import javax.persistence.*;

@Entity
public class SamplingRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long channelId, segmentId; private Double percentage; private Boolean enabled = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Long getSegmentId() { return segmentId; }
    public void setSegmentId(Long segmentId) { this.segmentId = segmentId; }
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
