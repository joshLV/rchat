package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.DepartmentView;
import com.rchat.platform.domain.DepartmentViewId;
import com.rchat.platform.domain.Group;

public interface DepartmentViewService extends GenericService<DepartmentView, DepartmentViewId> {
	/**
	 * 无分页查询指定部门所有下级部门
	 * 
	 * @param parent
	 *            父级部门
	 * @return 下级部门列表
	 */
	List<Department> findChildren(Department parent);

	/**
	 * 分页查询指定部门所有下级部门
	 * 
	 * @param parent
	 *            父级部门
	 * @param pageable
	 *            分页信息
	 * @return 子部门分页
	 */
	Page<Department> findChildren(Department parent, Pageable pageable);

	Page<Department> search(Group group, Department parent, Optional<String> name, Optional<String> aName,
			Optional<String> aUsername, Pageable pageable);

	/**
	 * 查找指定父级部门下属指定id子部门
	 * 
	 * @param id
	 *            子部门id
	 * @param parent
	 *            父级部门
	 * @return 子部门
	 */
	Optional<Department> findOneUnderDepartment(String id, Department parent);

}
