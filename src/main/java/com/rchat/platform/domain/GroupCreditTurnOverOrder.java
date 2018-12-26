package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("group")
public class GroupCreditTurnOverOrder extends CreditTurnOverOrder {
    private static final long serialVersionUID = 3764453308096500964L;
    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnoreProperties({"children", "administrator", "creator", "businesses", "departments", "agent", "server"})
    private Group group;

    public GroupCreditTurnOverOrder() {
    }

    public GroupCreditTurnOverOrder(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
