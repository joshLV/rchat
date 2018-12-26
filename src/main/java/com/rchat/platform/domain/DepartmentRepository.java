package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, DepartmentRepositoryCustom {

	/**
	 * 集团下属全部部门
	 * 
	 * @param group
	 * @return
	 */
	List<Department> findByGroup(Group group);

	/**
	 * 这个接口应该没什么意思，不会调用
	 * 
	 * @return
	 */
	List<Department> findByParentIsNull();

	/**
	 * 指定部门的下属部门
	 * 
	 * @param department
	 * @return
	 */
	List<Department> findByParent(Department department);

	/**
	 * 集团直属部门
	 * 
	 * @param group
	 * @return
	 */
	List<Department> findByGroupAndParentIsNull(Group group);

	/**
	 * 根据管理员查找部门或者公司，公司也是一种部门
	 * 
	 * @param user
	 *            公司或者部门管理员
	 * @return
	 */
	Optional<Department> findByAdministrator(User user);

	/**
	 * 查询所属集团指定部门的直接子部门
	 * 
	 * @param group
	 *            所属集团
	 * @param parent
	 *            上级部门
	 * @return 子部门列表
	 */
	List<Department> findByGroupAndParent(Group group, Department parent);

	/**
	 * 查询所属集团下指定Id的部门
	 * 
	 * @param group
	 *            所属集团
	 * @param id
	 *            指定部门id
	 * @return
	 */
	Optional<Department> findByGroupAndId(Group group, String id);
}
