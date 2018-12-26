package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentViewRepository
		extends JpaRepository<DepartmentView, DepartmentViewId>, DepartmentViewRepositoryCustom {
	Page<DepartmentView> findByViewIdParentId(String parentId, Pageable pageable);

	Optional<DepartmentView> findByViewId(DepartmentViewId departmentViewId);

	List<DepartmentView> findByViewIdParentId(String id);
}
