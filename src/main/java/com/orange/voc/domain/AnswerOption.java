package com.orange.voc.domain;
import javax.persistence.*;

@Entity @Table(name = "answer_option")
public class AnswerOption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Integer position; private String textEn; private String textAr; private String value; private String emoji;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public String getTextEn() { return textEn; }
    public void setTextEn(String textEn) { this.textEn = textEn; }
    public String getTextAr() { return textAr; }
    public void setTextAr(String textAr) { this.textAr = textAr; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
}
