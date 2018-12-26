package com.rchat.platform.domain;

import com.rchat.platform.aop.ResourceType;

public class Message<T> {
	private MessageType type;
	private ResourceType resource;
	private T source;

	public Message() {
	}

	public Message(MessageType type, ResourceType resource, T source) {
		this.type = type;
		this.resource = resource;
		this.source = source;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public ResourceType getResource() {
		return resource;
	}

	public void setResource(ResourceType resource) {
		this.resource = resource;
	}

	public T getSource() {
		return source;
	}

	public void setSource(T source) {
		this.source = source;
	}

}
