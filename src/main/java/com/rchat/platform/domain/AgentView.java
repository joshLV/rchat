package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "v_agent")
public class AgentView extends TimestampedResource implements Serializable, Comparable<AgentView> {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private AgentViewId viewId;
	/**
	 * 代理商名称
	 */
	@Column(length = 100, nullable = false)
	private String name;
	/**
	 * 联系人
	 */
	@Column(length = 100)
	private String linkman;
	/**
	 * 联系电话
	 */
	@Column(length = 100)
	private String phone;
	/**
	 * 联系邮箱
	 */
	@Column(length = 100)
	private String email;
	/**
	 * 代理区域
	 */
	@Column(length = 100)
	private String region;

	/**
	 * 额度单价
	 */
	@Column(scale = 2)
	private Double creditUnitPrice;

	/**
	 * 管理员
	 */
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "administrator", updatable = false)
	private User administrator;
	/**
	 * 创建者
	 */
	@ManyToOne
	@JoinColumn(name = "creator", nullable = false)
	private User creator;

	/**
	 * 代理商类型
	 */
	@Enumerated(EnumType.ORDINAL)
	private AgentType type = AgentType.NORMAL;

	/**
	 * 代理商等级
	 */
	private int level;

	@Transient
	public String getId() {
		return viewId.getId();
	}

	@JsonIgnore
	public AgentViewId getViewId() {
		return viewId;
	}

	public void setViewId(AgentViewId id) {
		this.viewId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Double getCreditUnitPrice() {
		return creditUnitPrice;
	}

	public void setCreditUnitPrice(Double creditUnitPrice) {
		this.creditUnitPrice = creditUnitPrice;
	}

	public User getAdministrator() {
		return administrator;
	}

	public void setAdministrator(User administrator) {
		this.administrator = administrator;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int compareTo(AgentView o) {
		int c = this.viewId.getParentId().compareTo(o.viewId.getParentId());
		return c == 0 ? this.level - o.level : c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((viewId == null) ? 0 : viewId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentView other = (AgentView) obj;
		if (viewId == null) {
			if (other.viewId != null)
				return false;
		} else if (!viewId.equals(other.viewId))
			return false;
		return true;
	}

}
