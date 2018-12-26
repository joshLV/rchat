package com.rchat.platform.domain;

import com.rchat.platform.common.DateUtils;
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

public class GroupRepositoryImpl implements GroupRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Group> search(Optional<String> agentId, Optional<String> name, Optional<String> aName,
                              Optional<String> aUsername, Optional<Boolean> status, Pageable pageable) {

        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Group> eQuery = builder.createQuery(Group.class);
        CriteriaQuery<Long> cQuery = builder.createQuery(Long.class);

        Root<Group> eRoot = eQuery.from(Group.class);
        Root<Group> cRoot = cQuery.from(Group.class);

        List<Predicate> ePredicates = new ArrayList<>();
        List<Predicate> cPredicates = new ArrayList<>();

        agentId.ifPresent(id -> {
            Predicate p;

            p = builder.equal(eRoot.get(Group_.agent).get(Agent_.id), id);
            ePredicates.add(p);
            p = builder.equal(cRoot.get(Group_.agent).get(Agent_.id), id);
            cPredicates.add(p);
        });
        name.ifPresent(n -> {
            Predicate p;
            n = "%" + n + "%";

            p = builder.like(eRoot.get(Group_.name), n);
            ePredicates.add(p);
            p = builder.like(cRoot.get(Group_.name), n);
            cPredicates.add(p);
        });
        status.ifPresent(s -> {
            Predicate p;

            p = builder.equal(eRoot.get(Group_.status), s);
            ePredicates.add(p);
            p = builder.equal(cRoot.get(Group_.status), s);
            cPredicates.add(p);
        });
        aUsername.ifPresent(n -> {
            Predicate p;
            n = "%" + n + "%";

            p = builder.like(eRoot.get(Group_.administrator).get(User_.username), n);
            ePredicates.add(p);
            p = builder.like(cRoot.get(Group_.administrator).get(User_.username), n);
            cPredicates.add(p);
        });
        aName.ifPresent(n -> {
            Predicate p;
            n = "%" + n + "%";

            p = builder.like(eRoot.get(Group_.administrator).get(User_.name), n);
            ePredicates.add(p);
            p = builder.like(cRoot.get(Group_.administrator).get(User_.name), n);
            cPredicates.add(p);
        });

        Predicate[] conditions;

        conditions = cPredicates.toArray(new Predicate[0]);
        cQuery = cQuery.select(builder.count(cRoot.get(Group_.id))).where(conditions);
        Long total = em.createQuery(cQuery).getSingleResult();

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = new ArrayList<>();
        Optional.ofNullable(pageable.getSort()).ifPresent(s -> s.forEach(o -> {
            if (o.isAscending())
                orders.add(builder.asc(eRoot.get(o.getProperty())));
            else
                orders.add(builder.desc(eRoot.get(o.getProperty())));
        }));

        conditions = ePredicates.toArray(new Predicate[0]);
        eQuery.where(conditions).orderBy(orders);

        List<Group> groups = em.createQuery(eQuery).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(groups, pageable, total);
    }

    @Override
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Group> countRoot = countQuery.from(Group.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(server).ifPresent(s -> predicates.add(builder.equal(countRoot.get(Group_.server), s)));
        countQuery.select(builder.count(countRoot.get(Group_.id))).where(predicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<Group> root = query.from(Group.class);

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(builder.tuple(root.get(Group_.id), root.get(Group_.updatedAt))).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
                .collect(Collectors.toList());

        return new PageImpl<>(briefs, pageable, total);
    }

    @Override
    public long countTotalAmount(Agent agent) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Group> countRoot = countQuery.from(Group.class);

        List<Predicate> countPredicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(countRoot.get(Group_.agent), a)));

        countQuery.select(builder.count(countRoot.get(Group_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countNewAmount(Agent agent) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Group> countRoot = countQuery.from(Group.class);

        List<Predicate> countPredicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(countRoot.get(Group_.agent), a)));

        countPredicates.add(builder.greaterThanOrEqualTo(countRoot.get(Group_.createdAt), DateUtils.currentDateStartTime()));

        countQuery.select(builder.count(countRoot.get(Group_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }
}
