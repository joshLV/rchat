package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 业务租用，也就是使用期限
 *
 * @author dzhang
 */
@Entity
@Table(name = "t_business_rent")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BusinessRent extends TimestampedResource implements Serializable {

    private static final long serialVersionUID = -8456671259465319009L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "business_id", updatable = false)
    @NotNull
    private Business business;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date deadline;

    @ManyToOne
    @JoinColumn(name = "talkback_user_id", updatable = false)
    private TalkbackUser user;

    /**
     * 充值月份数
     */
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private int creditMonths;

    public BusinessRent() {
    }

    public BusinessRent(String id) {
        this.id = id;
    }

    public BusinessRent(TalkbackUser user, Business defaultBussiness) {
        this.user = user;
        this.business = defaultBussiness;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public TalkbackUser getUser() {
        return user;
    }

    public void setUser(TalkbackUser user) {
        this.user = user;
    }

    public int getCreditMonths() {
        return creditMonths;
    }

    public void setCreditMonths(int creditMonths) {
        this.creditMonths = creditMonths;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BusinessRent))
            return false;
        BusinessRent other = (BusinessRent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
