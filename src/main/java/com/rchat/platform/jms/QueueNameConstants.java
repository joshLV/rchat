package com.rchat.platform.jms;

/**
 * 队列名称
 * 
 * @author dzhang
 */
public interface QueueNameConstants {
	String AGENT_CREATE = "rchat.queue.agent.create";
	String AGENT_DELETE = "rchat.queue.agent.delete";
	String AGENT_UPDATE = "rchat.queue.agent.update";

	String GROUP_CREATE = "rchat.queue.group.create";
	String GROUP_DELETE = "rchat.queue.group.delete";
	String GROUP_UPDATE = "rchat.queue.group.update";

	String DEPARTMENT_CREATE = "rchat.queue.department.create";
	String DEPARTMENT_DELETE = "rchat.queue.department.delete";
	String DEPARTMENT_UPDATE = "rchat.queue.department.update";

	String TALKBACK_GROUP_CREATE = "rchat.queue.talkback-group.create";
	String TALKBACK_GROUP_DELETE = "rchat.queue.talkback-group.delete";
	String TALKBACK_GROUP_UPDATE = "rchat.queue.talkback-group.update";

	String TALKBACK_USER_CREATE = "rchat.queue.talkback-user.create";
	String TALKBACK_USER_DELETE = "rchat.queue.taklback-user.delete";
	String TALKBACK_USER_UPDATE = "rchat.queue.talkback-user.update";
}
