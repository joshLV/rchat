package com.rchat.platform.web.controller.api.view;

import java.util.Date;

public class RenewLogView {
    private static final long serialVersionUID = -4956711814088459986L;
    private String id;
    /**
     * 账号
     */
    private String number;
    /**
     * 激活日期
     */
    private Date activatedAt;
    /**
     * 充值日期
     */
    private Date renewAt;
    /**
     * 所属集团
     */
    private String group;
    /**
     * 所属代理商
     */
    private String agent;

    private Date deadline;
    
    /**
     * 基础业务充值金额
     */
    private long basicCredit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Date getRenewAt() {
        return renewAt;
    }

    public void setRenewAt(Date renewAt) {
        this.renewAt = renewAt;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }

	public long getBasicCredit() {
		return basicCredit;
	}

	public void setBasicCredit(long basicCredit) {
		this.basicCredit = basicCredit;
	}
    
}
