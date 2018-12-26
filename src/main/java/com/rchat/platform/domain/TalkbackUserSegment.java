package com.rchat.platform.domain;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 对讲账户号段
 * 
 * @author dzhang
 *
 */
// @Entity
// @DiscriminatorValue("talkback_user")
public class TalkbackUserSegment extends Segment {
	private static final long serialVersionUID = 3222083631454491510L;

	@OneToOne
	@JoinColumn(name = "talkback_user_id")
	@JsonIgnoreProperties({ "groups", "group", "department", "businessRents", "permission", "segment" })
	private TalkbackUser owner;

	public TalkbackUser getOwner() {
		return owner;
	}

	public void setOwner(TalkbackUser owner) {
		this.owner = owner;
	}
}
