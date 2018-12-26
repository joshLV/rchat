package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * 集团额度账户
 *
 * @author dzhang
 */
@Entity
@DiscriminatorValue("group")
public class GroupCreditAccount extends CreditAccount {

    private static final long serialVersionUID = 291480138794822415L;

    @OneToOne
    @JoinColumn(name = "group_id")
    @JsonIgnoreProperties({"departments", "administrator", "creator", "agent", "segment", "businesses"})
    @NotNull
    private Group group;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public LogDetail toLogDetail() {
        LogDetail detail = new LogDetail();
        detail.setResourceId(String.valueOf(getId()));
        detail.setResourceName(String.format("%s 额度账户 当前额度：%s", group.getName(), getCredit()));
        detail.setTableName("t_credit_accout");

        return detail;
    }

    @Override
    public String getName() {
        if (group != null)
            return group.getName();
        return "";
    }
}
