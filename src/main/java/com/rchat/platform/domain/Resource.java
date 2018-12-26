package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 将系统可以控制访问权限的皆成为资源
 * 
 * @author dzhang
 * @since 2017-02-24 11:08:08
 */
@Entity
@Table(name = "t_resource")
public class Resource implements Serializable, Comparable<Resource> {
	private static final long serialVersionUID = 6392067658223143412L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;
	/**
	 * 资源名称
	 */
	@Column(nullable = false)
	private String name;

	private String description;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "resource_group_id")
	private ResourceGroup group;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ResourceGroup getGroup() {
		return group;
	}

	public void setGroup(ResourceGroup group) {
		this.group = group;
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
		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Resource [id=%s, name=%s, description=%s]", id, name, description);
	}

	@Override
	public int compareTo(Resource o) {
		return name.compareTo(o.name);
	}

}
