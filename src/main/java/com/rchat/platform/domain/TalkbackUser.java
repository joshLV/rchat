package com.rchat.platform.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 对讲用户
 * 
 * @author dzhang
 */
@Entity
@Table(name = "t_talkback_user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TalkbackUser extends TimestampedResource implements Serializable, Loggable {
	private static final long serialVersionUID = 6539193278193135432L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	/**
	 * 对讲号码
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "talkback_number_id", updatable = false, nullable = false)
	private TalkbackNumber number;

	/**
	 * 中文名
	 */
	@Column(length = 50)
	private String name;

	/**
	 * 密码
	 */
	@Column(length = 50)
	private String password;

	/**
	 * 对讲用户角色
	 */
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private TalkbackRole role = TalkbackRole.TALKBACK_USER;
	/**
	 * 部代表
	 */
	private boolean representative;

	/**
	 * 是否激活
	 */
	private boolean activated;

	/**
	 * 激活时间
	 */
	private Date activatedAt;

	/**
	 * 是否启用
	 */
	private boolean enabled = true;

	/**
	 * 对讲账户所属集团
	 */
	@ManyToOne
	@JoinColumn(name = "group_id", updatable = false)
	@JsonIgnoreProperties({ "departments", "businesses", "administrator", "creator", "segments" })
	private Group group;

	/**
	 * 使用期限，业务租用合同
	 */
	@NotNull
	@OneToMany(mappedBy = "user",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<BusinessRent> businessRents;

	/**
	 * 对讲账号权限
	 */
	@Embedded
	@NotNull
	private TalkbackPermission permission;
	
	/**
	 * 账号优先级 群组优先级分为1-10级，10级最高，1级最低  账号 默认优先级为5级
	 */
	private Integer priority = 5;
	
	/**
	 * 设备类型
	 */
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private EquipmentType type = EquipmentType.INTERCOM;

	/**
	 * 对讲账户所属部门，如果部门为空，则说明是集团账户
	 */
	@ManyToOne
	@JoinColumn(name = "department_id")
	@JsonIgnoreProperties({ "parent", "administrator", "creator", "privilege", "group" })
	private Department department;
	/**
	 * 默认群组
	 */
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "t_talkback_user_group", uniqueConstraints = @UniqueConstraint(columnNames = { "talkback_user_id",
			"talkback_group_id" }), joinColumns = @JoinColumn(name = "talkback_user_id"), inverseJoinColumns = @JoinColumn(name = "talkback_group_id"))
	@Fetch(FetchMode.SUBSELECT)
	@JsonIgnoreProperties({ "group", "department", "users" })
	private List<TalkbackGroup> groups;

	/**
	 * 最后一次充值续费时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRenewed;
	
	@Column(length = 36)
	private String talkbackGroupId;

	/**
	 * 到期时间，直接对应基础默认业务的到期时间也就是语音业务的到期时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date deadline;
	
	private String fullValue;

	public TalkbackUser() {
	}

	public TalkbackUser(String id) {
		this.id = id;
	}

	public TalkbackUser(TalkbackUser user) {
		this.name = user.name;
		this.password = user.password;
		this.activated = user.activated;
		this.enabled = user.enabled;
		this.representative = user.representative;
		this.group = user.group;
		this.businessRents = user.businessRents;
		this.permission = user.permission;
		this.department = user.department;
		this.groups = user.groups;
		this.role = user.role;
		this.number = user.number;
		this.lastRenewed = user.lastRenewed;
		this.talkbackGroupId = user.talkbackGroupId;
		this.fullValue=user.fullValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<TalkbackGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<TalkbackGroup> groups) {
		this.groups = groups;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public TalkbackNumber getNumber() {
		return number;
	}

	public void setNumber(TalkbackNumber number) {
		this.number = number;
	}

	public TalkbackRole getRole() {
		return role;
	}

	public void setRole(TalkbackRole role) {
		this.role = role;
	}

	public boolean isRepresentative() {
		return representative;
	}

	public void setRepresentative(boolean representative) {
		this.representative = representative;
	}

	public List<BusinessRent> getBusinessRents() {
		return businessRents;
	}

	public void setBusinessRents(List<BusinessRent> businessRents) {
		this.businessRents = businessRents;
	}

	public TalkbackPermission getPermission() {
		return permission;
	}

	public void setPermission(TalkbackPermission permission) {
		this.permission = permission;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Date getActivatedAt() {
		return activatedAt;
	}

	public void setActivatedAt(Date activatedAt) {
		this.activatedAt = activatedAt;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getLastRenewed() {
		return lastRenewed;
	}

	public void setLastRenewed(Date lastRenewed) {
		this.lastRenewed = lastRenewed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TalkbackUser))
			return false;
		TalkbackUser other = (TalkbackUser) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("TalkbackUser [id=%s, name=%s]", id, name);
	}

	@Override
	public LogDetail toLogDetail() {
		LogDetail detail = new LogDetail();
		detail.setResourceId(String.valueOf(number));
		detail.setResourceName(name);
		detail.setTableName("t_talkback_user");

		return detail;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public EquipmentType getType() {
		return type;
	}

	public void setType(EquipmentType type) {
		this.type = type;
	}

	public String getTalkbackGroupId() {
		return talkbackGroupId;
	}

	public void setTalkbackGroupId(String talkbackGroupId) {
		this.talkbackGroupId = talkbackGroupId;
	}

	public String getFullValue() {
		return fullValue;
	}

	public void setFullValue(String fullValue) {
		this.fullValue = fullValue;
	}
	
}
