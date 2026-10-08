package com.orange.voc.domain;
import java.util.*;
import javax.persistence.*;

@Entity
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Integer position;
    private String type; // FREE_TEXT, SINGLE_CHOICE, MULTI_CHOICE, DROPDOWN, SLIDER, STAR_RATING, NUMERIC_RATING, YES_NO, INFO, EMOJI_RATING, CUSTOM
    private Boolean mandatory = false;
    @Column(columnDefinition = "TEXT") private String textEn;
    @Column(columnDefinition = "TEXT") private String textAr;
    @Column(columnDefinition = "TEXT") private String configJson;
    @Column(columnDefinition = "TEXT") private String branchingJson;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false) @OrderBy("position")
    private Set<AnswerOption> options = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
    public String getTextEn() { return textEn; }
    public void setTextEn(String textEn) { this.textEn = textEn; }
    public String getTextAr() { return textAr; }
    public void setTextAr(String textAr) { this.textAr = textAr; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
    public String getBranchingJson() { return branchingJson; }
    public void setBranchingJson(String branchingJson) { this.branchingJson = branchingJson; }
    public Set<AnswerOption> getOptions() { return options; }
    public void setOptions(Set<AnswerOption> options) { this.options = options; }
}
