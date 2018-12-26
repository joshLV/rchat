package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 对讲账号号码
 * 
 * @author dzhang
 *
 */
@Entity
@Table(name = "t_talkback_number")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TalkbackNumber extends TimestampedResource implements Serializable {

	private static final long serialVersionUID = 904556579884042460L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 36)
	private String id;

	/**
	 * 乐观锁
	 */
	@Version
	@JsonIgnore
	private int version;

	/**
	 * 所属集团号段
	 */
	@ManyToOne
	@JoinColumn(name = "group_segment_id", updatable = false)
	@JsonIgnoreProperties({ "group" })
	private GroupSegment groupSegment;

	/**
	 * 号码
	 */
	@Column(updatable = false)
	private Integer value;

	@Column(updatable = false, unique = true)
	private String fullValue;

	/**
	 * 短号码
	 */
	@Column(length = 20, updatable = false)
	private String shortValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public GroupSegment getGroupSegment() {
		return groupSegment;
	}

	public void setGroupSegment(GroupSegment groupSegment) {
		this.groupSegment = groupSegment;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getFullValue() {
		if (fullValue == null) {
			fullValue = toLocalString();
		}
		return fullValue;
	}

	public void setFullValue(String fullValue) {
		this.fullValue = fullValue;
	}

	public String getShortValue() {
		return shortValue;
	}

	public void setShortValue(String shortValue) {
		this.shortValue = shortValue;
	}

	@Override
	public String toString() {
		return String.format("%s-%04d", groupSegment, value);
	}

	public String toLocalString() {
		return String.format("%s%04d", groupSegment.getFullValue(), value);
	}
}
