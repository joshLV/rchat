package com.rchat.platform.domain;


import javax.persistence.*;

@Entity
@DiscriminatorValue("credit-account")
public class CreditAccountRenewLog extends RenewLog {
    /**
     * 额度充值到这个账号
     */
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "to_credit_account_id")
    private CreditAccount toCreditAccount;

    public CreditAccount getToCreditAccount() {
        return toCreditAccount;
    }

    public void setToCreditAccount(CreditAccount toCreditAccount) {
        this.toCreditAccount = toCreditAccount;
    }
}
