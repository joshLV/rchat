package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 额度订单
 *
 * @author dzhang
 */
@Entity
@Table(name = "t_credit_order")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "disc", discriminatorType = DiscriminatorType.STRING, length = 20)
public class CreditOrder extends TimestampedResource implements Serializable {

    private static final long serialVersionUID = -6247299603134767561L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    /**
     * 最晚到账时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date deadline;

    /**
     * 最后一次续费时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date renewTime;

    /**
     * 订单状态
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CreditOrderStatus status = CreditOrderStatus.UNREVIEWED;

    /**
     * 已经下发的额度
     */
    @Min(0)
    private long distributedCredit;
    /**
     * 剩余额度
     */
    @Min(0)
    private long remanentCredit;

    /**
     * 向谁提出购买申请，一定是个代理商，超级受理台也是个代理商
     */
    @ManyToOne
    @JoinColumn(name = "respondent_id")
    @JsonIgnoreProperties({"parent", "children", "groups", "creator", "administrator"})
    private Agent respondent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getRenewTime() {
        return renewTime;
    }

    public void setRenewTime(Date renewTime) {
        this.renewTime = renewTime;
    }

    public CreditOrderStatus getStatus() {
        return status;
    }

    public void setStatus(CreditOrderStatus status) {
        this.status = status;
    }

    /**
     * 订单额度
     */
    @Transient
    public long getCredit() {
        return this.distributedCredit + this.remanentCredit;
    }

    public void setCredit(long credit) {
        this.remanentCredit = credit;
        this.distributedCredit = 0;
    }

    public long getDistributedCredit() {
        return distributedCredit;
    }

    public void setDistributedCredit(long distributedCredit) {
        this.distributedCredit = distributedCredit;
    }

    public long getRemanentCredit() {
        return remanentCredit;
    }

    public void setRemanentCredit(long remanentCredit) {
        this.remanentCredit = remanentCredit;
    }

    public Agent getRespondent() {
        return respondent;
    }

    public void setRespondent(Agent respondent) {
        this.respondent = respondent;
    }

    /**
     * 下发额度
     *
     * @param credit 下发额度数量
     */
    public void distribute(long credit) {
        // 还需要下发的额度小于下发数量
        if (this.remanentCredit < credit) {
            return;
        }

        this.distributedCredit += credit;
        this.remanentCredit -= credit;

        // 更新额度订单状态
        this.status = this.remanentCredit > 0 ? CreditOrderStatus.PARTIAL : CreditOrderStatus.COMPLETED;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CreditOrder other = (CreditOrder) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("CreditOrder [id=%s, credit=%s, deadline=%s, status=%s]", id, getCredit(), deadline,
                status);
    }

}
