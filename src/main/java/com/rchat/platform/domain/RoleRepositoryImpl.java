package com.rchat.platform.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class RoleRepositoryImpl implements RoleRepositoryCustom {
	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Role> search(String name) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Role> roleQuery = builder.createQuery(Role.class);
		Root<Role> roleRoot = roleQuery.from(Role.class);

		roleQuery.where(builder.like(roleRoot.get(Role_.name), "%" + name + "%"));

		return em.createQuery(roleQuery).getResultList();
	}
}
