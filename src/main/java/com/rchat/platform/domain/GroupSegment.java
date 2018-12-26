package com.rchat.platform.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * 集团号段
 * 
 * @author dzhang
 */
@Entity
@DiscriminatorValue("group")
public class GroupSegment extends Segment {

	private static final long serialVersionUID = 7199727794173745902L;

	/**
	 * 所属集团
	 */
	@ManyToOne
	@JoinColumn(name = "group_id")
	@JsonIgnoreProperties({ "departments", "administrator", "creator", "agent", "segments", "businesses" })
	private Group group;

	/**
	 * 所属代理商号段
	 */
	@ManyToOne
	@JoinColumn(name = "agent_segment_id")
	@JsonIgnoreProperties({ "agent" })
	@NotNull
	private AgentSegment agentSegment;

	public GroupSegment() {
	}

	public GroupSegment(Segment segment) {
		this.setId(segment.getId());
		this.setValue(segment.getValue());
		this.setVip(segment.isVip());
	}

	public GroupSegment(String id) {
		this.setId(id);
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public AgentSegment getAgentSegment() {
		return agentSegment;
	}

	public void setAgentSegment(AgentSegment agentSegment) {
		this.agentSegment = agentSegment;
	}

	public String getFullValue() {
		return String.format("%s%04d", agentSegment, getValue());
	}

	@Transient
	@JsonIgnoreProperties({ "children", "parent", "administrator", "creator" })
	@JsonProperty(access = Access.READ_ONLY)
	public Agent getAgent() {
		if (agentSegment != null) {
			return agentSegment.getAgent();
		}
		return null;
	}

	@Override
	public String toString() {
		if (group != null && group.isVip()) {
			return String.format("%06d", getValue());
		}
		return String.format("%s-%04d", agentSegment, getValue());
	}

	public String toLocalString() {
		if (group != null && group.isVip()) {
			return String.format("%06d", getValue());
		}
		return String.format("%s%04d", agentSegment, getValue());
	}

}
