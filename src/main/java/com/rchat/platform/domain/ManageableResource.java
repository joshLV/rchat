package com.rchat.platform.domain;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * 受管资源
 * 
 * @author dzhang
 *
 */
@MappedSuperclass
public abstract class ManageableResource {
	/**
	 * 管理员
	 */
	@ManyToOne
	@JoinColumn(name = "adminstrator", nullable = false)
	private User administrator;
	/**
	 * 创建者
	 */
	@ManyToOne
	@JoinColumn(name = "creator", nullable = false)
	private User creator;

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
}
