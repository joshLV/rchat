package com.rchat.platform.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 额度账户
 *
 * @author dzhang
 */
@Entity
@Table(name = "t_credit_account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "disc", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class CreditAccount extends TimestampedResource implements Serializable, Loggable {

    private static final long serialVersionUID = -6619334600218391430L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    /**
     * 剩余额度
     */
    private long credit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    @Transient
    public void addCredit(long credit) {
        this.credit += credit;
    }

    /**
     * 消费额度
     *
     * @param credit
     */
    public void reduceCredit(long credit) {
        this.credit -= credit;
    }

    @Transient
    public abstract String getName();

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
        if (getClass() != obj.getClass())
            return false;
        CreditAccount other = (CreditAccount) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("CreditAccount [id=%s, credit=%s]", id, credit);
    }

}
