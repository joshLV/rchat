package com.rchat.platform.domain;

import java.util.List;

public interface RoleRepositoryCustom {
	List<Role> search(String name);
}
