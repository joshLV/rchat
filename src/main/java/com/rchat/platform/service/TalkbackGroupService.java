package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import com.rchat.platform.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TalkbackGroupService extends GenericService<TalkbackGroup, String> {

	Page<TalkbackGroup> findByGroup(Group group, Pageable pageable);

	void setTalbackUsers(TalkbackGroup group);

	Page<TalkbackGroup> findByDepartments(List<Department> departments, Pageable pageable);

	Page<TalkbackGroup> findByDepartment(Department department, Pageable pageable);

	Page<TalkbackGroup> search(Optional<Group> group, Optional<Department> department, Optional<String> name,
			Pageable pageable);

	Optional<TalkbackGroup> findByDepartmentAndType(Department department, TalkbackGroupType type);

	Page<Brief> findBriefs(Server server, Pageable pageable);
}
