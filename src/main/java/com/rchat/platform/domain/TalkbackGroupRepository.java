package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkbackGroupRepository extends JpaRepository<TalkbackGroup, String>, TalkbackGroupRepositoryCustom {

	Page<TalkbackGroup> findByGroup(Group group, Pageable pageable);

	Page<TalkbackGroup> findByDepartmentIn(List<Department> departments, Pageable pageable);

	Page<TalkbackGroup> findByDepartment(Department department, Pageable pageable);

	Optional<TalkbackGroup> findByDepartmentAndType(Department department, TalkbackGroupType type);

}
