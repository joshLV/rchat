package com.rchat.platform.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "t_task_configuration")
public class TaskConfiguration extends TimestampedResource implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	/**
	 * 同步时间
	 */
	private Date synDate;
	/**
	 * 语音视频服务器
	 */
	private String avServer;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getSynDate() {
		return synDate;
	}

	public void setSynDate(Date synDate) {
		this.synDate = synDate;
	}

	public String getAvServer() {
		return avServer;
	}

	public void setAvServer(String avServer) {
		this.avServer = avServer;
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
		TaskConfiguration other = (TaskConfiguration) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("TaskConfiguration [id=%s, synDate=%s, avServer=%s]", id, synDate, avServer);
	}

}
