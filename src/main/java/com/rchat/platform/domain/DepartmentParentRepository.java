package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentParentRepository extends JpaRepository<DepartmentParent, String> {

	List<DepartmentParent> findByDepartmentId(String id);

	List<DepartmentParent> findByParentId(String id);

	Optional<DepartmentParent> findByDepartmentIdAndLevel(String id, int level);

	List<DepartmentParent> findByParentIdAndLevel(String id, int level);
}
