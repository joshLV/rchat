package com.rchat.platform.web.controller;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dzhang
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -441186709496146571L;

	private int type;
	private String content;
	private Date timestamp;

	public Message() {
		this(0);
	}

	public Message(int type) {
		this.type = type;
		this.timestamp = new Date();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
