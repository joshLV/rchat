package com.rchat.platform.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * 用户信息，没有用户必须要拥有一个角色，创建用户的时候，这个用户没有任何角色，即没有任何权限
 *
 * @author dzhang
 * @since 2017-02-24 14:30:25
 */
@Entity
@Table(name = "t_user", indexes = { @Index(columnList = "email"), @Index(columnList = "name"),
		@Index(columnList = "phone"), @Index(columnList = "username", unique = true) })
public class User extends TimestampedResource implements Serializable, Comparable<User>, Loggable {
	private static final long serialVersionUID = 265864182386944519L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;
	/**
	 * 账户名
	 */
	@Column(nullable = false, length = 100)
	@NotNull
	private String username;
	/**
	 * 密码 MD5 加密
	 */
	@Column(nullable = false, length = 50, updatable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	/**
	 * 加密盐值
	 */
	@Column(nullable = false, length = 50, updatable = false)
	@JsonIgnore
	private String salt;
	/**
	 * 名字
	 */
	@Column(length = 100)
	private String name;
	/**
	 * 邮箱
	 */
	@Column(length = 100)
	private String email;

	/**
	 * 是否在线
	 */
	private boolean online = false;
	/**
	 * 是否激活
	 */
	private boolean activated = true;
	/**
	 * 是否可用
	 */
	private boolean enabled = true;
	/**
	 * 是否过期
	 */
	private boolean expired = false;

	private String phone;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_user_role", uniqueConstraints = {
			@UniqueConstraint(columnNames = { "user_id", "role_id" }) }, joinColumns = {
					@JoinColumn(nullable = false, name = "user_id") }, inverseJoinColumns = {
							@JoinColumn(nullable = false, name = "role_id") })
	@Fetch(FetchMode.SUBSELECT)
	private List<Role> roles;

	@Transient
	private boolean encoded;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
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
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("User [id=%s, username=%s, name=%s]", id, username, name);
	}

	@Override
	public int compareTo(User o) {
		int result = name.compareTo(o.name);
		if (result == 0) {
			return username.compareTo(o.username);
		}
		return result;
	}

	@Override
	public LogDetail toLogDetail() {
		LogDetail detail = new LogDetail();
		detail.setResourceName(String.format("平台用户： username = %s, name = %s", username, name));
		detail.setTableName("t_user");
		detail.setResourceId(id);
		return detail;
	}

}
