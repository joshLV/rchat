package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import com.rchat.platform.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 部门管理业务
 * 
 * @author dzhang
 *
 */
public interface DepartmentService extends GenericService<Department, String> {
	/**
	 * 查找部门树
	 * 
	 * @param department
	 *            父部门
	 * @param depth
	 *            树深度
	 * @return 部门树
	 */
	List<Department> findDepartmentTree(Department department, int depth);

	/**
	 * 查询集团下属所有部门
	 * 
	 * @param group
	 *            集团
	 * @return 直属部门列表
	 */
	List<Department> findByGroup(Group group);

	/**
	 * 查询指定集团的直属部门
	 * 
	 * @param group
	 * @return
	 */
	List<Department> findByGroupDirectlyUnder(Group group);

	/**
	 * 查询指定部门的上级部门
	 * 
	 * @param child
	 * @return
	 */
	Optional<Department> findParent(Department child);

	/**
	 * 根据管理员查询部门
	 * 
	 * @param user
	 * @return
	 */
	Optional<Department> findByAdministrator(User user);

	/**
	 * 根据集团查询指定部门直接子部门
	 * 
	 * @param group
	 *            部门所属集团
	 * @param parent
	 *            上级部门
	 * @return 集团列表
	 */
	List<Department> findByGroupAndParent(Group group, Department parent);

	/**
	 * 查询指定集团下属指定id的部门
	 * 
	 * @param group
	 *            所属集团
	 * @param id
	 *            部门id
	 * @return
	 */
	Optional<Department> findByGroupAndId(Group group, String id);

	Page<Department> search(Group group, Optional<String> departmentName, Optional<String> adminName,
			Optional<String> adminUsername, Pageable pageable);

	Page<Brief> findBriefs(Server server, Pageable pageable);
}
