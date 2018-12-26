package com.rchat.platform.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

/**
 * 日志详细，一个业务日志，有可能包含多个日志详细
 * 
 * @author dzhang
 *
 */
@Entity
@Table(name = "t_log_detail")
public class LogDetail extends TimestampedResource implements Serializable, Comparable<LogDetail> {

	private static final long serialVersionUID = -3761512666985068762L;
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "log_id")
	@JsonIgnore
	private Log log;

	private String resourceId;
	private String resourceName;
	private String operation;
	private String description;
	private String tableName;
	private int step;

	public LogDetail() {
		Date now = new Date();
		setCreatedAt(now);
		setUpdatedAt(now);
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public String toString() {
		return String.format("LogDetail [resource=%s#(%s), operation=%s, description=%s]", resourceId, resourceName,
				operation, description);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LogDetail))
			return false;
		LogDetail other = (LogDetail) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(LogDetail o) {
		int x = this.getCreatedAt().compareTo(o.getCreatedAt());
		if (x == 0) {
			return this.getStep() - o.getStep();
		} else {
			return x;
		}
	}

}
