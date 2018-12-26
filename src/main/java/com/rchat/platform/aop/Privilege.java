package com.rchat.platform.aop;

public class Privilege {
	private ResourceType resource;
	private OperationType operation;

	public Privilege(ResourceType resource, OperationType operation) {
		this.resource = resource;
		this.operation = operation;
	}

	public ResourceType getResource() {
		return resource;
	}

	public OperationType getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return String.format("%s %s", operation, resource);
	}
}
