package com.rchat.platform.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentRepositoryCustom {
	Optional<Department> findParent(Department department);

	Page<Department> search(Group group, Optional<String> name, Optional<String> aName, Optional<String> aUsername,
			Pageable pageable);

	Page<Brief> findBriefs(Server server, Pageable pageable);
}
