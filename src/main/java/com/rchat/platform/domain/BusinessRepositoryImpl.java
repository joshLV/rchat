package com.rchat.platform.domain;

import com.rchat.platform.common.RchatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessRepositoryImpl implements BusinessRepositoryCustom {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessRepositoryImpl.class);
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Brief> findBriefs(Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Business> countRoot = countQuery.from(Business.class);

        countQuery.select(builder.count(countRoot.get(Business_.id)));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<Business> root = query.from(Business.class);

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(builder.tuple(root.get(Business_.id), root.get(Business_.updatedAt))).orderBy(orders);

        List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
                .collect(Collectors.toList());

        return new PageImpl<>(briefs, pageable, total);
    }

    @Override
    public void setEnabled(Boolean enabled, List<String> ids) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaUpdate<Business> update = builder.createCriteriaUpdate(Business.class);
        Root<Business> root = update.from(Business.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = root.get(Business_.id).in(ids);
        predicates[1] = builder.equal(root.get(Business_.internal), Boolean.FALSE);

        update.set(root.get(Business_.enabled), enabled).where(predicates);

        int updated = em.createQuery(update).executeUpdate();

        LOGGER.info("updated business is {}.", updated);
    }
}
