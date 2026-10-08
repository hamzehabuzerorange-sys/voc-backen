package com.orange.voc.domain;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

@Entity
public class Survey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true) private String code; private String name;
    @Enumerated(EnumType.STRING) private SurveyStatus status = SurveyStatus.DRAFT;
    private String languageMode = "SELECTABLE"; // SELECTABLE | FIXED | AUTOMATIC
    private String fixedLanguage;
    private Long welcomeMessageId, themeId, segmentId;
    private LocalDateTime startAt, endAt;
    private Integer validityDays;
    private String expiryBehavior = "BLOCK"; // BLOCK | ALLOW_LATE
    private Boolean requireMandatory = false;
    private Boolean defaultForAllChannels = false;
    private Integer version = 1;
    private String createdBy, approvedBy;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "survey_id", nullable = false) @OrderBy("position")
    private Set<Question> questions = new LinkedHashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "survey_channel", joinColumns = @JoinColumn(name = "survey_id"), inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<Channel> channels = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SurveyStatus getStatus() { return status; }
    public void setStatus(SurveyStatus status) { this.status = status; }
    public String getLanguageMode() { return languageMode; }
    public void setLanguageMode(String languageMode) { this.languageMode = languageMode; }
    public String getFixedLanguage() { return fixedLanguage; }
    public void setFixedLanguage(String fixedLanguage) { this.fixedLanguage = fixedLanguage; }
    public Long getWelcomeMessageId() { return welcomeMessageId; }
    public void setWelcomeMessageId(Long welcomeMessageId) { this.welcomeMessageId = welcomeMessageId; }
    public Long getThemeId() { return themeId; }
    public void setThemeId(Long themeId) { this.themeId = themeId; }
    public Long getSegmentId() { return segmentId; }
    public void setSegmentId(Long segmentId) { this.segmentId = segmentId; }
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }
    public String getExpiryBehavior() { return expiryBehavior; }
    public void setExpiryBehavior(String expiryBehavior) { this.expiryBehavior = expiryBehavior; }
    public Boolean getRequireMandatory() { return requireMandatory; }
    public void setRequireMandatory(Boolean requireMandatory) { this.requireMandatory = requireMandatory; }
    public Boolean getDefaultForAllChannels() { return defaultForAllChannels; }
    public void setDefaultForAllChannels(Boolean defaultForAllChannels) { this.defaultForAllChannels = defaultForAllChannels; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public Set<Question> getQuestions() { return questions; }
    public void setQuestions(Set<Question> questions) { this.questions = questions; }
    public Set<Channel> getChannels() { return channels; }
    public void setChannels(Set<Channel> channels) { this.channels = channels; }
}
