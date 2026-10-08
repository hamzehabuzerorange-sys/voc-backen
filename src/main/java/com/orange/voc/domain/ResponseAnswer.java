package com.orange.voc.domain;
import javax.persistence.*;

@Entity @Table(name = "response_answer")
public class ResponseAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long questionId, optionId;
    @Column(columnDefinition = "TEXT") private String valueText;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    public String getValueText() { return valueText; }
    public void setValueText(String valueText) { this.valueText = valueText; }
}
