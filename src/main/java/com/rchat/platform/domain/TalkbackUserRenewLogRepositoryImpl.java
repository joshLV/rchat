package com.rchat.platform.domain;

import com.rchat.platform.common.RchatUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.*;

public class TalkbackUserRenewLogRepositoryImpl implements TalkbackUserRenewLogRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<TalkbackUserRenewLog> statRenewLogs(Agent agent, Group group, Date start, Date end, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUserRenewLog> query = builder.createQuery(TalkbackUserRenewLog.class);

        Root<TalkbackUserRenewLog> countRoot = countQuery.from(TalkbackUserRenewLog.class);
        Root<TalkbackUserRenewLog> root = query.from(TalkbackUserRenewLog.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.group).get(Group_.agent), a));
            predicates.add(builder.equal(root.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.group).get(Group_.agent), a));
        });
        Optional.ofNullable(group).ifPresent(g -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.group), g));
            predicates.add(builder.equal(root.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.group), g));
        });
        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });
        Optional.ofNullable(start).ifPresent(s -> {
            countPredicates.add(builder.greaterThanOrEqualTo(countRoot.get(TalkbackUserRenewLog_.createdAt), s));
            predicates.add(builder.greaterThanOrEqualTo(root.get(TalkbackUserRenewLog_.createdAt), s));
        });
        Optional.ofNullable(end).ifPresent(e -> {
            countPredicates.add(builder.lessThanOrEqualTo(countRoot.get(TalkbackUserRenewLog_.createdAt), e));
            predicates.add(builder.lessThanOrEqualTo(root.get(TalkbackUserRenewLog_.createdAt), e));
        });

        countQuery.select(builder.count(countRoot.get(TalkbackUserRenewLog_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUserRenewLog> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<TalkbackUserRenewLog> findRenewLogs(Agent agent, Group group, Date start, Date end, String fullValue) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUserRenewLog> query = builder.createQuery(TalkbackUserRenewLog.class);

        Root<TalkbackUserRenewLog> root = query.from(TalkbackUserRenewLog.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.group), g)));
        Optional.ofNullable(fullValue).ifPresent(n -> predicates.add(builder.equal(root.get(TalkbackUserRenewLog_.user).get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n)));
        Optional.ofNullable(start).ifPresent(s -> predicates.add(builder.greaterThanOrEqualTo(root.get(TalkbackUserRenewLog_.createdAt), s)));
        Optional.ofNullable(end).ifPresent(e -> predicates.add(builder.lessThanOrEqualTo(root.get(TalkbackUserRenewLog_.createdAt), e)));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }
}
