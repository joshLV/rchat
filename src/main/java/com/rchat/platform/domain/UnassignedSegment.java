package com.rchat.platform.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 未指定主体的
 * 
 * @author dzhang
 *
 */
@Entity
@DiscriminatorValue("unassigned")
public class UnassignedSegment extends Segment {
	private static final long serialVersionUID = -7926774739521598940L;

	public UnassignedSegment() {
	}

	public UnassignedSegment(String id) {
		this.setId(id);
	}

}
