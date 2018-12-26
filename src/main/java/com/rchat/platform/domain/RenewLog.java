package com.rchat.platform.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 充值日志，记录什么时候，从哪个额度账号向哪个对讲账号充值多少额度
 */
@Entity
@Table(name = "t_renew_log")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "disc", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class RenewLog extends TimestampedResource {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;
    /**
     * 充值账号，从此账号扣除额度
     */
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "credit_account_id")
    private CreditAccount creditAccount;
    /**
     * 充值金额
     */
    private long credit;
    
    /**
     * 基础业务充值金额
     */
    private long basicCredit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getBasicCredit() {
		return basicCredit;
	}

	public void setBasicCredit(long basicCredit) {
		this.basicCredit = basicCredit;
	}

	public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }
}
