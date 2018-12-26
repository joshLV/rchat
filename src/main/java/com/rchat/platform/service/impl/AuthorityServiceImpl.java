package com.rchat.platform.service.impl;

import com.rchat.platform.domain.Authority;
import com.rchat.platform.domain.AuthorityRepository;
import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl extends AbstractService<Authority, String> implements AuthorityService {

	@Autowired
	private AuthorityRepository repository;

	@Override
	protected JpaRepository<Authority, String> repository() {
		return repository;
	}

	@Override
	public List<Authority> findByRole(Role role) {
		return repository.findByRole(role);
	}

	@Override
    @Cacheable(cacheNames = "authority", key = "#root.args[0].id")
    public List<Authority> findByUser(User user) {
		return findByRole(user.getRoles());
	}

	@Override
	public List<Authority> findByRole(List<Role> roles) {
		return repository.findByRoleIn(roles);
	}

}
