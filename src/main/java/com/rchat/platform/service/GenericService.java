package com.rchat.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface GenericService<T, ID extends Serializable> {
	/**
	 * 操作成功的
	 */
	boolean SUCCEED = true;
	/**
	 * 操作失败的
	 */
	boolean FAILED = false;

	/**
	 * 指定Id的实体是否存在
	 * 
	 * @param id
	 *            实体id
	 * @return 如果存在，<code>true</code>，不存在，<code>false</code>
	 */
	boolean exists(ID id);

	/**
	 * 根据样例查询指定实体是否存在
	 * 
	 * @param example
	 * @return
	 */
	boolean exists(Example<T> example);

	/**
	 * 保存实体
	 * 
	 * @param t
	 * @return
	 */
	T create(T entity);

	List<T> create(List<T> entities);

	/**
	 * 更新实体模型
	 * 
	 * @param t
	 * @return
	 */
	T update(T entity);

	/**
	 * 更新全部实体
	 * 
	 * @param entities
	 * @return
	 */
	List<T> update(List<T> entities);

	/**
	 * 删除实体模型
	 * 
	 * @param t
	 * @return
	 */
	boolean delete(T entity);

	/**
	 * 根据ID删除实体模型
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(ID id);

	/**
	 * 删除全部实体
	 * 
	 * @param entities
	 * @return
	 */
	boolean delete(List<T> entities);

	/**
	 * 删除所有数据
	 * 
	 * @return
	 */
	boolean deleteAll();

	/**
	 * 根据ID查询实体
	 * 
	 * @param id
	 * @return
	 */
	Optional<T> findOne(ID id);

	/**
	 * 
	 * 
	 * @param example
	 * @return
	 */
	Optional<T> findOne(Example<T> example);

	/**
	 * 查询全部实体模型
	 * 
	 * @return
	 */
	List<T> findAll();

	/**
	 * 分页查找
	 * 
	 * @param pageable
	 *            分页参数
	 * @return
	 */
	Page<T> findAll(Pageable pageable);

	List<T> findAll(Sort sort);

	List<T> findAll(Example<T> example);

	Page<T> findAll(Example<T> example, Pageable pageable);

	List<T> findAll(Example<T> example, Sort sort);

	List<T> findAll(List<ID> ids);

	List<T> findAllInternal(List<ID> ids);

}
