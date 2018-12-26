package com.rchat.platform.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.collection.internal.PersistentBag;
import org.springframework.util.ClassUtils;

/**
 * 资源分组，资源以分组的形式存在，这样便于管理
 *
 * @author dzhang
 * @since 2017-02-27 15:57:15
 */
@Entity
@Table(name = "t_resource_group")
public class ResourceGroup implements Serializable, Comparable<ResourceGroup> {
	private static final long serialVersionUID = 6776284392427175013L;
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;
	@Column(nullable = false)
	private String name;
	private String description;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<Resource> resources;

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

	public List<Resource> getResources() {
		if (ClassUtils.isAssignableValue(PersistentBag.class, resources)) {
			PersistentBag bag = (PersistentBag) resources;
			Object value = bag.getValue();
			System.err.println(value);
		}

		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	@Override
	public String toString() {
		return String.format("ResourceGroup [id=%s, name=%s, description=%s]", id, name, description);
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
		ResourceGroup other = (ResourceGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(ResourceGroup o) {
		return name.compareTo(o.name);
	}

}
