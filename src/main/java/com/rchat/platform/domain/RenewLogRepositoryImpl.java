package com.rchat.platform.domain;

import com.rchat.platform.common.DateUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class RenewLogRepositoryImpl implements RenewLogRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public long countConsumption(CreditAccount account, Date start, Date end) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<RenewLog> root = query.from(RenewLog.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(RenewLog_.creditAccount), account));
        Optional.ofNullable(start).ifPresent(s -> predicates.add(builder.greaterThanOrEqualTo(root.get(RenewLog_.createdAt), s)));
        Optional.ofNullable(end).ifPresent(e -> predicates.add(builder.lessThanOrEqualTo(root.get(RenewLog_.createdAt), e)));

        CriteriaQuery<Long> countQuery = query.select(builder.count(root.get(RenewLog_.id))).where(predicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();
        if (count == 0) {
            return 0;
        }

        query.select(builder.sum(root.get(RenewLog_.credit))).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getSingleResult();
    }

    @Override
    public long countTodayConsumption(CreditAccount account) {
        return countConsumption(account, DateUtils.currentDateStartTime(), null);
    }

    @Override
    public List<RenewLog> findConsumption(CreditAccount account, Date start, Date end) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<RenewLog> query = builder.createQuery(RenewLog.class);
        Root<RenewLog> root = query.from(RenewLog.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(RenewLog_.creditAccount), account));
        Optional.ofNullable(start).ifPresent(s -> predicates.add(builder.greaterThanOrEqualTo(root.get(RenewLog_.createdAt), s)));
        Optional.ofNullable(end).ifPresent(e -> predicates.add(builder.lessThanOrEqualTo(root.get(RenewLog_.createdAt), e)));


        query.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }
}
