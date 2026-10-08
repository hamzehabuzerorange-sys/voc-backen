package com.orange.voc.domain;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

@Entity @Table(name = "survey_response")
public class SurveyResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long invitationId, staticLinkId, surveyId; private Integer surveyVersion;
    private String language; private Boolean afterExpiry = false;
    private LocalDateTime submittedAt = LocalDateTime.now();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private List<ResponseAnswer> answers = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInvitationId() { return invitationId; }
    public void setInvitationId(Long invitationId) { this.invitationId = invitationId; }
    public Long getStaticLinkId() { return staticLinkId; }
    public void setStaticLinkId(Long staticLinkId) { this.staticLinkId = staticLinkId; }
    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public Integer getSurveyVersion() { return surveyVersion; }
    public void setSurveyVersion(Integer surveyVersion) { this.surveyVersion = surveyVersion; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Boolean getAfterExpiry() { return afterExpiry; }
    public void setAfterExpiry(Boolean afterExpiry) { this.afterExpiry = afterExpiry; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public List<ResponseAnswer> getAnswers() { return answers; }
    public void setAnswers(List<ResponseAnswer> answers) { this.answers = answers; }
}
