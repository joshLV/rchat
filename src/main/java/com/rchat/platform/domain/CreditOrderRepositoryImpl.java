package com.rchat.platform.domain;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CreditOrderRepositoryImpl implements CreditOrderRepositoryCustom {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private JdbcTemplate jdbc;

	@Override
	public Page<CreditOrder> findByRespondentOrAgent(Agent agent, Pageable pageable) {
		String countSql = "select count(1) from t_credit_order where respondent_id = ? or agent_id = ?";
		ResultSetExtractor<Long> extractor = (r) -> {
			if (r.next())
				return r.getLong(1);
			return Long.valueOf(0);
		};
		long total = jdbc.query(countSql, new Object[] { agent.getId(), agent.getId() }, extractor);

		if (total == 0) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		String sql = "select * from t_credit_order where respondent_id = ?1 or agent_id = ?2 order by created_at desc";
		Query query = em.createNativeQuery(sql, CreditOrder.class);
		query.setParameter(1, agent.getId());
		query.setParameter(2, agent.getId());

		query.setMaxResults(pageable.getPageSize());
		query.setFirstResult(pageable.getOffset());

		@SuppressWarnings("unchecked")
		List<CreditOrder> orders = query.getResultList();

		return new PageImpl<>(orders, pageable, total);
	}

}
