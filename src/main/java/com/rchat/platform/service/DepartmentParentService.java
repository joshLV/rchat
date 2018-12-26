package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.DepartmentParent;

public interface DepartmentParentService extends GenericService<DepartmentParent, String> {
	/**
	 * 查找指定部门所有的上级部门
	 * 
	 * @param child
	 *            子部门
	 * @return 上级部门列表
	 */
	List<DepartmentParent> findParents(Department child);

	/**
	 * 查找指定父部门的所有下级部门
	 * 
	 * @param parent
	 *            父部门
	 * @return 子部门列表
	 */
	List<DepartmentParent> findChildren(Department parent);

	/**
	 * 查找指定部门的指定级别的父级部门
	 * 
	 * @param child
	 *            子部门
	 * @param level
	 *            父部门级别
	 * @return 指定级别的父级部门
	 */
	Optional<DepartmentParent> findSpecifiedLevelParent(Department child, int level);

	/**
	 * 查找指定部门的指定级别的子部门
	 * 
	 * @param parent
	 *            父部门
	 * @param level
	 *            子部门级别
	 * @return 同级别子部门列表
	 */
	List<DepartmentParent> findSpecifiedLevelChild(Department parent, int level);
}
