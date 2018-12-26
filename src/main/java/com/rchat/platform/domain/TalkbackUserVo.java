package com.rchat.platform.domain;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class TalkbackUserVo implements Serializable, RowMapper<TalkbackUserVo>{
	private String id;
	private String full_value;
	private String short_value;
	private String role;
	private String user_name;
	private String agent_name;
	private String group_name;
	private String department_name;
	private String activated;
	private Date deadline;
	private String agent_id;
	private String group_id;
	private String department_id;
	private String numberId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFull_value() {
		return full_value;
	}
	public void setFull_value(String full_value) {
		this.full_value = full_value;
	}
	public String getShort_value() {
		return short_value;
	}
	public void setShort_value(String short_value) {
		this.short_value = short_value;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getAgent_name() {
		return agent_name;
	}
	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getDepartment_name() {
		return department_name;
	}
	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
	public String getActivated() {
		return activated;
	}
	public void setActivated(String activated) {
		this.activated = activated;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public String getAgent_id() {
		return agent_id;
	}
	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getDepartment_id() {
		return department_id;
	}
	public void setDepartment_id(String department_id) {
		this.department_id = department_id;
	}

	
	public String getNumberId() {
		return numberId;
	}
	public void setNumberId(String numberId) {
		this.numberId = numberId;
	}
	@Override
	public TalkbackUserVo mapRow(ResultSet rs, int rowNum) throws SQLException {
		TalkbackUserVo talkbackUserVo=new TalkbackUserVo();
		talkbackUserVo.setId(rs.getString("id"));
		talkbackUserVo.setFull_value(rs.getString("full_value"));
		talkbackUserVo.setShort_value(rs.getString("short_value"));
		talkbackUserVo.setRole(rs.getString("role"));
		talkbackUserVo.setUser_name(rs.getString("user_name"));
		talkbackUserVo.setAgent_name(rs.getString("agent_name"));
		talkbackUserVo.setGroup_name(rs.getString("group_name"));
		talkbackUserVo.setDepartment_name(rs.getString("department_name"));
		talkbackUserVo.setActivated(rs.getString("activated"));
		talkbackUserVo.setDeadline(rs.getDate("deadline"));
		talkbackUserVo.setAgent_id(rs.getString("agent_id"));
		talkbackUserVo.setNumberId(rs.getString("numberId"));
		talkbackUserVo.setGroup_id(rs.getString("group_id"));
		talkbackUserVo.setDepartment_id(rs.getString("department_id"));
		return talkbackUserVo;
	}

}
