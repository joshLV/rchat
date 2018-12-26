package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 授权以角色为单位，不可直接对用户直接授权，将来要是想对用户直接授权的话，应该为这个用户建一个单独的角色
 *
 * @author dzhang
 * @since 2017-02-24 13:25:28
 */
@Entity
@Table(name = "t_authority")
public class Authority implements Serializable {

	private static final long serialVersionUID = -8745373434417358522L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private Role role;
	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private Resource resource;
	/**
	 * 操作权限：0000b 没权限 0001b 查询权限
	 * <table style="width: 100%; border-collapse: collapse" border="1">
	 * <thead>
	 * <tr>
	 * <th width="50">序号</th>
	 * <th>编码</th>
	 * <th>描述</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>1</td>
	 * <td>0000</td>
	 * <td>无任何权限</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>0001</td>
	 * <td>查询</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>0010</td>
	 * <td>修改</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>0100</td>
	 * <td>删除</td>
	 * </tr>
	 * <tr>
	 * <td>5</td>
	 * <td>1000</td>
	 * <td>增加</td>
	 * </tr>
	 * <tr>
	 * <td>6</td>
	 * <td>1111</td>
	 * <td>全部权限</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	private int operations;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public int getOperations() {
		return operations;
	}

	public void setOperations(int operations) {
		this.operations = operations;
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
		Authority other = (Authority) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Authority [id=%s, role=%s, resource=%s, operations=%s]", id, role.getName(),
				resource.getName(), operations);
	}

}
