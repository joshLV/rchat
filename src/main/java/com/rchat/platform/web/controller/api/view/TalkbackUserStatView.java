package com.rchat.platform.web.controller.api.view;

import java.io.Serializable;
import java.util.Date;

/**
 * 对讲账号统计信息视图
 * 账号、中文名、所属集团、所属代理、激活时间、账号有效期
 */
public class TalkbackUserStatView implements Serializable {
    private static final long serialVersionUID = -718931434930436466L;
    private String id;
    private String fullValue;
    private String name;
    private String agent;
    private String group;
    private Date activatedAt;
    private Date deadline;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullValue() {
        return fullValue;
    }

    public void setFullValue(String fullValue) {
        this.fullValue = fullValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
