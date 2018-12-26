package com.rchat.platform.common;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.domain.Message;
import com.rchat.platform.domain.MessageType;

public class MessageBuilder<T> {
	private MessageType type;
	private ResourceType resource;
	private T source;

	public MessageBuilder<T> resource(ResourceType resource) {
		this.resource = resource;
		return this;
	}

	public MessageBuilder<T> source(T source) {
		this.source = source;
		return this;
	}

	public MessageBuilder<T> create() {
		this.type = MessageType.CREATE;
		return this;
	}

	public MessageBuilder<T> modify() {
		this.type = MessageType.MODIFY;
		return this;
	}

	public MessageBuilder<T> delete() {
		this.type = MessageType.DELETE;
		return this;
	}

	public Message<T> build() {
		return new Message<>(type, resource, source);
	}
}
