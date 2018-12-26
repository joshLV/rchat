package com.rchat.platform.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("talkback-user")
public class TalkbackUserRenewLog extends RenewLog {
    /**
     * 充值的对讲账号，额度会变成时间，充值到这个对讲账号中
     */
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "talkback_user_id")
    private TalkbackUser user;

    public TalkbackUser getUser() {
        return user;
    }

    public void setUser(TalkbackUser user) {
        this.user = user;
    }

    public Date getActivatedAt() {
        return user.getActivatedAt();
    }

    public String getNumber() {
        return user.getNumber().getFullValue();
    }

    public Date getDeadline() {
        return user.getDeadline();
    }

    public Group getGroup() {
        return user.getGroup();
    }

    public Agent getAgent() {
        return user.getGroup().getAgent();
    }

}
