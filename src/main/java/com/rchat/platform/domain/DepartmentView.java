package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "v_department")
public class DepartmentView extends TimestampedResource implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@JsonIgnore
	private DepartmentViewId viewId;
	/**
	 * 部门名称
	 */
	@Column(nullable = false, length = 100)
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
	 * 所属集团，如果group_id 不为空，说明是集团直属的部门
	 */
	@ManyToOne
	@JoinColumn(name = "group_id", updatable = false)
	@JsonIgnoreProperties({ "agent", "administrator", "creator", "businesses" })
	private Group group;
	/**
	 * 管理员
	 */
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "administrator", nullable = false)
	@JsonIgnoreProperties({ "roles" })
	private User administrator;
	/**
	 * 创建者
	 */
	@ManyToOne
	@JoinColumn(name = "creator", updatable = false)
	@JsonIgnoreProperties({ "roles" })
	private User creator;

	/**
	 * 部门权限
	 */
	private DepartmentPrivilege privilege;

	// 默认是一级部门，也就是集团直属部门
	private int level = 1;

	public String getId() {
		return viewId.getId();
	}

	@JsonIgnore
	public DepartmentViewId getViewId() {
		return viewId;
	}

	public void setViewId(DepartmentViewId viewId) {
		this.viewId = viewId;
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
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

	public DepartmentPrivilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(DepartmentPrivilege privilege) {
		this.privilege = privilege;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
