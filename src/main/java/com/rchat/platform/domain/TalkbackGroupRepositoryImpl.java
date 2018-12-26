package com.rchat.platform.domain;

import com.rchat.platform.common.RchatUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

public class TalkbackGroupRepositoryImpl implements TalkbackGroupRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<TalkbackGroup> search(Optional<Group> group, Optional<Department> department, Optional<String> name,
                                      Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackGroup> entityQuery = builder.createQuery(TalkbackGroup.class);

        Root<TalkbackGroup> countRoot = countQuery.from(TalkbackGroup.class);
        Root<TalkbackGroup> entityRoot = entityQuery.from(TalkbackGroup.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> entityPredicates = new ArrayList<>();

        group.ifPresent(g -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackGroup_.group).get(Group_.id), g.getId());
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackGroup_.group).get(Group_.id), g.getId());
            entityPredicates.add(predicate);
        });

        department.ifPresent(d -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackGroup_.department).get(Department_.id), d.getId());
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackGroup_.department).get(Department_.id), d.getId());
            entityPredicates.add(predicate);
        });

        name.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(TalkbackGroup_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(TalkbackGroup_.name), n);
            entityPredicates.add(predicate);
        });

        countQuery.select(builder.count(countRoot.get(TalkbackGroup_.id)))
                .where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        if (total.longValue() == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = new ArrayList<>();
        Optional.ofNullable(pageable.getSort()).ifPresent(s -> s.forEach(o -> {
            if (o.isAscending())
                orders.add(builder.asc(entityRoot.get(o.getProperty())));
            else
                orders.add(builder.desc(entityRoot.get(o.getProperty())));
        }));

        entityQuery.where(entityPredicates.toArray(new Predicate[0])).orderBy(orders);
        List<TalkbackGroup> groups = em.createQuery(entityQuery).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(groups, pageable, total);
    }

    @Override
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackGroup> countRoot = countQuery.from(TalkbackGroup.class);

        List<Predicate> predicates = new ArrayList<>();
        Optional.ofNullable(server).ifPresent(s -> predicates.add(builder.equal(countRoot.get(TalkbackGroup_.group).get(Group_.server), s)));

        countQuery.select(builder.count(countRoot.get(TalkbackGroup_.id))).where(predicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<TalkbackGroup> root = query.from(TalkbackGroup.class);

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(builder.tuple(root.get(TalkbackGroup_.id), root.get(TalkbackGroup_.updatedAt))).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
                .collect(Collectors.toList());

        return new PageImpl<>(briefs, pageable, total);
    }
}
