package com.rchat.platform.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentViewRepositoryCustom {
	Page<DepartmentView> search(Group group, Department parent, Optional<String> name, Optional<String> aName,
			Optional<String> aUsername, Pageable pageable);
}
