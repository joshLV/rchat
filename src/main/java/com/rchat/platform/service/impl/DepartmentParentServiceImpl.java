package com.rchat.platform.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.DepartmentParent;
import com.rchat.platform.domain.DepartmentParentRepository;
import com.rchat.platform.service.DepartmentParentService;

@Service
public class DepartmentParentServiceImpl extends AbstractService<DepartmentParent, String>
		implements DepartmentParentService {
	@Autowired
	private DepartmentParentRepository repository;

	@Override
	public List<DepartmentParent> findParents(Department child) {
		if (child == null) {
			return Collections.emptyList();
		}

		return repository.findByDepartmentId(child.getId());
	}

	@Override
	public List<DepartmentParent> findChildren(Department parent) {
		return repository.findByParentId(parent.getId());
	}

	@Override
	public Optional<DepartmentParent> findSpecifiedLevelParent(Department child, int level) {
		if (level > child.getLevel()) {
			return Optional.empty();
		}

		return repository.findByDepartmentIdAndLevel(child.getId(), level);
	}

	@Override
	public List<DepartmentParent> findSpecifiedLevelChild(Department parent, int level) {
		if (level < parent.getLevel()) {
			return Collections.emptyList();
		}

		return repository.findByParentIdAndLevel(parent.getId(), level);
	}

	@Override
	protected JpaRepository<DepartmentParent, String> repository() {
		return repository;
	}

}
