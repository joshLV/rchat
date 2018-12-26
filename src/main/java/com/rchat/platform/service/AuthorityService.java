package com.rchat.platform.service;

import java.util.List;

import com.rchat.platform.domain.Authority;
import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.User;

public interface AuthorityService extends GenericService<Authority, String> {

	/**
	 * 查找指定角色的全部可以访问的资源
	 * 
	 * @param role
	 *            待查询的角色
	 * @return 权限列表，如果没有没有权限，返回空列表
	 */
	List<Authority> findByRole(Role role);

	List<Authority> findByRole(List<Role> roles);

	List<Authority> findByUser(User user);
}
