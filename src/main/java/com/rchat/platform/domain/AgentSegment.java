package com.rchat.platform.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 代理商号段
 * 
 * @author dzhang
 *
 */
@Entity
@DiscriminatorValue("agent")
public class AgentSegment extends Segment {

	private static final long serialVersionUID = -5445927065506960493L;

	@OneToOne
	@JoinColumn(name = "agent_id")
	@JsonIgnoreProperties({ "groups", "children", "administrator", "creator", "parent", "segments" })
	private Agent agent;

	public AgentSegment() {
	}

	public AgentSegment(Segment segment) {
		this.setId(segment.getId());
		this.setValue(segment.getValue());
		this.setVip(segment.isVip());
	}

	public AgentSegment(String id) {
		this.setId(id);
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public String getFullValue() {
		return String.format("%04d", getValue());
	}
}
