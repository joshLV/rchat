package com.rchat.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "t_agent_parent")
public class AgentParent {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	/**
	 * 代理商id
	 */
	@Column(length = 36)
	private String agentId;
	/**
	 * 父级id
	 */
	@Column(length = 36)
	private String parentId;
	/**
	 * 父级代理商等级
	 */
	private Integer level;

	public AgentParent() {
	}

	public AgentParent(String agentId, String parentId, int level) {
		this.agentId = agentId;
		this.parentId = parentId;
		this.level = level;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
