package com.rchat.platform.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@DiscriminatorValue("group")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GroupCreditOrder extends CreditOrder implements Loggable {

	private static final long serialVersionUID = 8771392528090783717L;

	/**
	 * 订单申请人
	 */

	@ManyToOne
	@JoinColumn(name = "group_id")
	@JsonIgnoreProperties({ "children", "administrator", "creator", "businesses", "departments" })
	private Group group;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public LogDetail toLogDetail() {
		LogDetail detail = new LogDetail();
		detail.setResourceId(String.valueOf(getId()));
		detail.setResourceName(String.format("集团：%s %s 已下发额度： %s", group.getName(), "额度订单", getDistributedCredit()));
		detail.setTableName("t_credit_order");

		return detail;
	}

}
