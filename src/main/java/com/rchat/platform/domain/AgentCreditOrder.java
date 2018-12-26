package com.rchat.platform.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@DiscriminatorValue("agent")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AgentCreditOrder extends CreditOrder implements Loggable {

	private static final long serialVersionUID = 2378974400409344260L;

	@ManyToOne
	@JoinColumn(name = "agent_id")
	@JsonIgnoreProperties({ "groups", "children", "administrator", "creator", "parent", "segment" })
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
		detail.setResourceName(String.format("%s %s 已下发额度： %s", agent.getName(), "额度订单", getDistributedCredit()));
		detail.setTableName("t_credit_order");

		return detail;
	}

}
