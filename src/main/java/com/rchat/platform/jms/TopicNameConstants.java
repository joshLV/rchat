package com.rchat.platform.jms;

public interface TopicNameConstants {
    String AGENT_CREATE = "rchat.topic.agent.create";
    String AGENT_DELETE = "rchat.topic.agent.delete";
    String AGENT_UPDATE = "rchat.topic.agent.update";

    String GROUP_CREATE = "rchat.topic.group.create";
    String GROUP_DELETE = "rchat.topic.group.delete";
    String GROUP_UPDATE = "rchat.topic.group.update";

    String DEPARTMENT_CREATE = "rchat.topic.department.create";
    String DEPARTMENT_DELETE = "rchat.topic.department.delete";
    String DEPARTMENT_UPDATE = "rchat.topic.department.update";
    /**默认群组消息队列**/
    String TALKBACK_GROUP_CREATE = "rchat.topic.talkback-group.create";
    
    String TALKBACK_GROUP_DELETE = "rchat.topic.talkback-group.delete";
    String TALKBACK_GROUP_UPDATE = "rchat.topic.talkback-group.update";

    String TALKBACK_USER_CREATE = "rchat.topic.talkback-user.create";
    String TALKBACK_USER_DELETE = "rchat.topic.talkback-user.delete";
    String TALKBACK_USER_UPDATE = "rchat.topic.talkback-user.update";

    String TASK_CONFIG_CREATE = "rchat.topic.task-config.create";
    String TASK_CONFIG_DELETE = "rchat.topic.task-config.delete";
    String TASK_CONFIG_UPDATE = "rchat.topic.task-config.update";

    String BUSINESS_CREATE = "rchat.topic.business.create";
    String BUSINESS_DELETE = "rchat.topic.business.delete";
    String BUSINESS_UPDATE = "rchat.topic.business.update";

    String GROUP_SERVER_CHANGED = "rchat.topic.group.server.changed";
    String GROUP_FILE_DELETE = "rchat.topic.group.file.delete";
}
