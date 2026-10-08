package com.orange.voc.domain;
import javax.persistence.*;

@Entity
public class Channel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String code; private String name;
    private String deliveryMethod; // SMS | EMAIL | WHATSAPP | API
    private Boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
