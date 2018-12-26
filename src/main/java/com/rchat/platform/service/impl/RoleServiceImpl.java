package com.rchat.platform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.RoleRepository;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.RoleService;

@Service
@SecurityService(ResourceType.ROLE)
public class RoleServiceImpl extends AbstractService<Role, String> implements RoleService {
	@Autowired
	private RoleRepository repository;

	@Override
	public List<Role> search(String name) {
		List<Role> roles = repository.search(name);
		return roles;
	}

	@Override
	protected JpaRepository<Role, String> repository() {
		return repository;
	}

	@Override
	@SecurityMethod(false)
	public List<Role> findByUser(User user) {
		return repository.findByUser(user.getId());
	}
}
