package com.rchat.platform.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.User;
import com.rchat.platform.exception.UserIsNotAgentAdminException;
import com.rchat.platform.exception.UserIsNotDepartmentAdminException;
import com.rchat.platform.exception.UserIsNotGroupAdminException;
import com.rchat.platform.service.AgentService;
import com.rchat.platform.service.DepartmentService;
import com.rchat.platform.service.GroupService;

/**
 * 平台环境
 * 
 * @author dzhang
 *
 */
@Component
public class RchatEnv {
	@Autowired
	private GroupService groupService;
	@Autowired
	private AgentService agentService;
	@Autowired
	private DepartmentService departmentService;

	public Group currentGroup() {
		User user = RchatUtils.currentUser();

		return groupService.findByAdministrator(user).orElseThrow(UserIsNotGroupAdminException::new);
	}

	public Agent currentAgent() {
		User user = RchatUtils.currentUser();

		return agentService.findByAdministrator(user).orElseThrow(UserIsNotAgentAdminException::new);
	}

	public Department currentDepartment() {
		User user = RchatUtils.currentUser();

		return departmentService.findByAdministrator(user).orElseThrow(UserIsNotDepartmentAdminException::new);
	}

	public boolean agentBelongToCurrentUser(String agentId) {
		Agent agent = currentAgent();

		return agent.getId().equals(agentId);
	}

	public boolean groupBelongToCurrentUser(String groupId) {
		Group group = currentGroup();

		return group.getId().equals(groupId);
	}

	public boolean departmentBelongToCurrentUser(String departmentId) {
		Department department = currentDepartment();

		return department.getId().equals(departmentId);
	}
}
