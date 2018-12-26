package com.rchat.platform.service;

import java.util.List;

import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.User;

public interface RoleService extends GenericService<Role, String> {
	List<Role> search(String name);

	List<Role> findByUser(User user);
}
