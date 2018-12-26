package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 额度上缴订单
 */
@Entity
@Table(name = "t_credit_turn_over_order")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "disc", discriminatorType = DiscriminatorType.STRING, length = 20)
public class CreditTurnOverOrder extends TimestampedResource implements Serializable {
    private static final long serialVersionUID = -7036735927312644903L;
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    private Long credit;
    @Enumerated
    private TurnOverStatus status;
    @ManyToOne
    @JoinColumn(name = "respondent_id")
    @JsonIgnoreProperties({"parent", "administrator", "creator"})
    private Agent respondent;


    public CreditTurnOverOrder() {
    }

    public CreditTurnOverOrder(String id) {
        this.id = id;
    }

    public CreditTurnOverOrder(Long credit) {
        this.credit = credit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCredit() {
        return credit;
    }

    public void setCredit(Long credit) {
        this.credit = credit;
    }

    public TurnOverStatus getStatus() {
        return status;
    }

    public void setStatus(TurnOverStatus status) {
        this.status = status;
    }

    public Agent getRespondent() {
        return respondent;
    }

    public void setRespondent(Agent respondent) {
        this.respondent = respondent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CreditTurnOverOrder that = (CreditTurnOverOrder) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("CreditTurnOverOrder{id='%s', credit=%d, status=%s}", id, credit, status);
    }
}
