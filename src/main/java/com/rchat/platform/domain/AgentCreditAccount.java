package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * 代理商额度账户
 *
 * @author dzhang
 */
@Entity
@DiscriminatorValue("agent")
public class AgentCreditAccount extends CreditAccount {

    private static final long serialVersionUID = 1867357459420959139L;

    @OneToOne
    @JoinColumn(name = "agent_id")
    @JsonIgnoreProperties({"groups", "children", "administrator", "creator", "parent", "segment"})
    @NotNull
    private Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public LogDetail toLogDetail() {
        LogDetail detail = new LogDetail();
        detail.setResourceId(String.valueOf(getId()));
        detail.setResourceName(String.format("代理商 %s 额度账户 当前额度：%s", agent.getName(), getCredit()));
        detail.setTableName("t_credit_accout");

        return detail;
    }

    @Override
    public String getName() {
        if (agent != null)
            return agent.getName();
        return "";
    }
}
