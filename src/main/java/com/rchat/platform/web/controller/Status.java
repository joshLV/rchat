package com.rchat.platform.web.controller;

import java.io.Serializable;
import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class Status implements Serializable {

	private static final long serialVersionUID = -4927730147726193722L;
	private int code;
	private String message;
	@JsonFormat(shape = Shape.NUMBER)
	private Date timestamp = new Date();

	public Status() {
	}

	public Status(HttpStatus status, String message) {
		this(status.value(), message);
	}

	public Status(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setCode(HttpStatus status) {
		setCode(status.value());
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return String.format("Status [code=%s, message=%s, timestamp=%s]", code, message, timestamp);
	}
}
