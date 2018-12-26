package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("agent")
public class AgentCreditTurnOverOrder extends CreditTurnOverOrder {
    private static final long serialVersionUID = -5781602291179333370L;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    @JsonIgnoreProperties({"groups", "children", "administrator", "creator", "parent", "segment"})
    private Agent agent;

    public AgentCreditTurnOverOrder() {
    }

    public AgentCreditTurnOverOrder(Agent agent) {
        this.agent = agent;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
