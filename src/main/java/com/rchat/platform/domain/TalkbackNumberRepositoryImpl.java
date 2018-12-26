package com.rchat.platform.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.common.RchatUtils;

public class TalkbackNumberRepositoryImpl implements TalkbackNumberRepositoryCustom {
	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Brief> findBriefs(Pageable pageable) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<TalkbackNumber> countRoot = countQuery.from(TalkbackNumber.class);

		countQuery.select(builder.count(countRoot.get(TalkbackNumber_.id)));

		Long total = em.createQuery(countQuery).getSingleResult();
		if (total.longValue() == 0) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		CriteriaQuery<Tuple> query = builder.createTupleQuery();

		Root<TalkbackNumber> root = query.from(TalkbackNumber.class);

		List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
		query.select(builder.tuple(root.get(TalkbackNumber_.id), root.get(TalkbackNumber_.updatedAt))).orderBy(orders);

		List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
				.collect(Collectors.toList());

		return new PageImpl<>(briefs, pageable, total);
	}

}
