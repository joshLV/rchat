package com.rchat.platform.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.domain.TimestampedResource;
import com.rchat.platform.service.GenericService;

@Transactional(readOnly = true)
public abstract class AbstractService<T, ID extends Serializable> implements GenericService<T, ID> {
	@Override
	@Transactional
	public boolean delete(ID id) {
		repository().delete(id);
		return SUCCEED;
	}

	@Override
	@Transactional
	public boolean delete(T entity) {
		repository().delete(entity);
		return SUCCEED;
	}

	@Override
	public boolean deleteAll() {
		repository().deleteAllInBatch();
		return SUCCEED;
	}

	@Override
	@Transactional
	public boolean delete(List<T> entities) {
		repository().deleteInBatch(entities);
		return SUCCEED;
	}

	@Override
	@Transactional
	public T create(T entity) {
		if (TimestampedResource.class.isInstance(entity)) {
			Date now = new Date();
			TimestampedResource t = TimestampedResource.class.cast(entity);

			t.setUpdatedAt(now);
			t.setCreatedAt(now);
		}

		return repository().save(entity);
	}

	@Override
	@Transactional
	public List<T> create(List<T> entities) {
		Date now = new Date();
		entities.parallelStream().forEach(e -> {
			if (TimestampedResource.class.isInstance(e)) {
				TimestampedResource t = TimestampedResource.class.cast(e);

				t.setUpdatedAt(now);
				t.setCreatedAt(now);
			}
		});

		List<T> list = repository().save(entities);
		return list;
	}

	@Override
	public List<T> findAll() {
		return repository().findAll();
	}

	@Override
	public List<T> findAll(List<ID> ids) {
		return repository().findAll(ids);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return repository().findAll(pageable);
	}

	@Override
	public List<T> findAll(Sort sort) {
		return repository().findAll(sort);
	}

	@Override
	public List<T> findAll(Example<T> example) {
		return repository().findAll(example);
	}

	@Override
	public Page<T> findAll(Example<T> example, Pageable pageable) {
		return repository().findAll(example, pageable);
	}

	@Override
	public List<T> findAll(Example<T> example, Sort sort) {
		return repository().findAll(example, sort);
	}

	@Override
	public Optional<T> findOne(Example<T> example) {
		return Optional.ofNullable(repository().findOne(example));
	}

	@Override
	public Optional<T> findOne(ID id) {
		return Optional.ofNullable(repository().findOne(id));
	}

	@Override
	@Transactional
	public List<T> update(List<T> entities) {
		Date now = new Date();
		entities.parallelStream().forEach(e -> {
			if (TimestampedResource.class.isInstance(e))
				TimestampedResource.class.cast(e).setUpdatedAt(now);
		});

		return repository().save(entities);
	}

	@Override
	@Transactional
	public T update(T entity) {
		if (TimestampedResource.class.isInstance(entity))
			TimestampedResource.class.cast(entity).setUpdatedAt(new Date());

		return repository().save(entity);
	}

	@Override
	public boolean exists(ID id) {
		return repository().exists(id);
	}

	@Override
	public boolean exists(Example<T> example) {
		return repository().exists(example);
	}

	@Override
	@SecurityMethod(false)
	public List<T> findAllInternal(List<ID> ids) {
		return repository().findAll(ids);
	}

	/**
	 * 获取此业务接口对应的领域模型
	 * 
	 * @return
	 */
	protected abstract JpaRepository<T, ID> repository();
}
