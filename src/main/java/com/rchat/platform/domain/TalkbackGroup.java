package com.rchat.platform.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 对讲群组
 * 
 * @author dzhang
 *
 */
@Entity
@Table(name = "t_talkback_group")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TalkbackGroup extends TimestampedResource implements Serializable {
	private static final long serialVersionUID = -8272210303981641543L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	/**
	 * 对讲组名称
	 */
	@Column(length = 50)
	@NotNull
	private String name;

	/**
	 * 对讲群组类型
	 */
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private TalkbackGroupType type = TalkbackGroupType.PREDEFINED;

	/**
	 * 所属集团
	 */
	@ManyToOne
	@JoinColumn(name = "group_id")
	@NotNull
	@JsonIgnoreProperties({ "agent", "departments", "administrator", "creator", "businesses" })
	private Group group;

	/**
	 * 包含的对讲账号成员
	 */
	// @ManyToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	@Transient
	private List<TalkbackUser> users;

	/**
	 * 所属部门
	 */
	@ManyToOne
	@JoinColumn(name = "department_id")
	@JsonIgnoreProperties({ "parent", "group", "chilren", "administrator", "creator" })
	private Department department;

	/**
	 * 群组优先级 群组优先级分为1-10级，10级最高，1级最低 默认群组优先级为5级， 默认临时群组优先级为5级
	 */
	private Integer priority = 5;
	@Transient
	private long userCount;
	@Transient
	private String uid;
	@Transient
	private String token;

	/**
	 * 备注
	 */
	private String description;
	/**
	 * 唯一id
	 */
	private Integer groupOnlyId;

	public TalkbackGroup() {
	}

	public TalkbackGroup(String id) {
		this.id = id;
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

	public TalkbackGroupType getType() {
		return type;
	}

	public void setType(TalkbackGroupType type) {
		this.type = type;
	}

	public List<TalkbackUser> getUsers() {
		return users;
	}

	public void setUsers(List<TalkbackUser> users) {
		this.users = users;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getUserCount() {
		return userCount;
	}

	public void setUserCount(long userCount) {
		this.userCount = userCount;
	}


	public Integer getGroupOnlyId() {
		return groupOnlyId;
	}

	public void setGroupOnlyId(Integer groupOnlyId) {
		this.groupOnlyId = groupOnlyId;
	}
	

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Transient
	@JsonIgnoreProperties({ "administrator", "creator", "parent", "children", "groups", "segments" })
	@JsonProperty(access = Access.READ_ONLY)
	public Agent getAgent() {
		return this.group.getAgent();
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
		if (!(obj instanceof TalkbackGroup))
			return false;
		TalkbackGroup other = (TalkbackGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("TalkbackGroup [id=%s, name=%s, type=%s]", id, name, type);
	}
}
