package com.orange.voc.domain;
import javax.persistence.*;

@Entity @Table(name = "api_key")
public class ApiKey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name; private String keyHash; private Boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKeyHash() { return keyHash; }
    public void setKeyHash(String keyHash) { this.keyHash = keyHash; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
