package com.rchat.platform.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.common.RchatUtils;

public class DepartmentViewRepositoryImpl implements DepartmentViewRepositoryCustom {
	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<DepartmentView> search(Group group, Department parent, Optional<String> name, Optional<String> aName,
			Optional<String> aUsername, Pageable pageable) {

		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		CriteriaQuery<DepartmentView> entityQuery = builder.createQuery(DepartmentView.class);

		Root<DepartmentView> countRoot = countQuery.from(DepartmentView.class);
		Root<DepartmentView> entityRoot = entityQuery.from(DepartmentView.class);

		List<Predicate> countPredicates = new ArrayList<>();
		List<Predicate> entityPredicates = new ArrayList<>();

		countPredicates.add(builder.equal(countRoot.get(DepartmentView_.group), group));

		entityPredicates.add(builder.equal(entityRoot.get(DepartmentView_.group), group));

		countPredicates.add(
				builder.equal(countRoot.get(DepartmentView_.viewId).get(DepartmentViewId_.parentId), parent.getId()));
		entityPredicates.add(
				builder.equal(entityRoot.get(DepartmentView_.viewId).get(DepartmentViewId_.parentId), parent.getId()));

		name.ifPresent(n -> {
			Predicate predicate;
			n = "%" + n + "%";

			predicate = builder.like(countRoot.get(DepartmentView_.name), n);
			countPredicates.add(predicate);

			predicate = builder.like(entityRoot.get(DepartmentView_.name), n);
			entityPredicates.add(predicate);
		});

		aName.ifPresent(n -> {
			Predicate predicate;
			n = "%" + n + "%";

			predicate = builder.like(countRoot.get(DepartmentView_.administrator).get(User_.name), n);
			countPredicates.add(predicate);

			predicate = builder.like(entityRoot.get(DepartmentView_.administrator).get(User_.name), n);
			entityPredicates.add(predicate);
		});

		aUsername.ifPresent(n -> {
			Predicate predicate;
			n = "%" + n + "%";

			predicate = builder.like(countRoot.get(DepartmentView_.administrator).get(User_.username), n);
			countPredicates.add(predicate);

			predicate = builder.like(entityRoot.get(DepartmentView_.administrator).get(User_.username), n);
			entityPredicates.add(predicate);
		});

		countQuery.select(builder.count(countRoot.get(DepartmentView_.viewId).get(DepartmentViewId_.id)))
				.where(countPredicates.toArray(new Predicate[0]));
		Long total = em.createQuery(countQuery).getSingleResult();

		if (total.longValue() == 0) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, entityRoot);

		entityQuery.where(entityPredicates.toArray(new Predicate[0])).orderBy(orders);

		List<DepartmentView> departments = em.createQuery(entityQuery).setFirstResult(pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		return new PageImpl<>(departments, pageable, total);
	}

}
