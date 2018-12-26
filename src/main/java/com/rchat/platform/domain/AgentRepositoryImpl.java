package com.rchat.platform.domain;

import com.rchat.platform.common.DateUtils;
import com.rchat.platform.common.RchatUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

public class AgentRepositoryImpl implements AgentRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Agent> search(Optional<String> name, Optional<String> adminName, Optional<String> adminUsername,
                              Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        // 先查总数
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<Agent> agentQuery = builder.createQuery(Agent.class);

        Root<Agent> countRoot = countQuery.from(Agent.class);
        Root<Agent> agentRoot = agentQuery.from(Agent.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> agentPredicates = new ArrayList<>();

        name.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(Agent_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(agentRoot.get(Agent_.name), n);
            agentPredicates.add(predicate);
        });

        adminName.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(Agent_.administrator).get(User_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(agentRoot.get(Agent_.administrator).get(User_.name), n);
            agentPredicates.add(predicate);
        });

        adminUsername.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(Agent_.administrator).get(User_.username), n);
            countPredicates.add(predicate);

            predicate = builder.like(agentRoot.get(Agent_.administrator).get(User_.username), n);
            agentPredicates.add(predicate);
        });

        countQuery.select(builder.count(countRoot.get(Agent_.id))).where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = new ArrayList<>();
        Optional.ofNullable(pageable.getSort()).ifPresent(s -> s.forEach(o -> {
            if (o.isAscending())
                orders.add(builder.asc(agentRoot.get(o.getProperty())));
            else
                orders.add(builder.desc(agentRoot.get(o.getProperty())));
        }));

        agentQuery.where(agentPredicates.toArray(new Predicate[0])).orderBy(orders);

        List<Agent> agents = em.createQuery(agentQuery).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(agents, pageable, total);
    }

    @Override
    public Optional<Agent> findByGroup(Group group) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Agent> agentQuery = builder.createQuery(Agent.class);
        Root<Agent> agentRoot = agentQuery.from(Agent.class);

        Subquery<String> agentSubquery = agentQuery.subquery(String.class);
        Root<Group> groupRoot = agentSubquery.from(Group.class);
        agentSubquery.select(groupRoot.get(Group_.agent).get(Agent_.id))
                .where(builder.equal(groupRoot.get(Group_.id), group.getId()));

        agentQuery.select(agentRoot).where(agentRoot.get(Agent_.id).in(agentSubquery));

        Agent agent = em.createQuery(agentQuery).getSingleResult();
        return Optional.ofNullable(agent);
    }

    @Override
    public Optional<Agent> findParent(Agent child) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Agent> query = builder.createQuery(Agent.class);
        Root<Agent> root = query.from(Agent.class);

        Subquery<String> subquery = query.subquery(String.class);
        Root<Agent> subroot = subquery.from(Agent.class);

        subquery.select(subroot.get(Agent_.parent).get(Agent_.id))
                .where(builder.equal(subroot.get(Agent_.id), child.getId()));

        query.select(root).where(root.get(Agent_.id).in(subquery)).distinct(true);

        try {
            Agent agent = em.createQuery(query).getSingleResult();
            return Optional.of(agent);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<Brief> findBriefs(Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Agent> countRoot = countQuery.from(Agent.class);

        countQuery.select(builder.count(countRoot.get(Agent_.id)));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<Agent> root = query.from(Agent.class);

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(builder.tuple(root.get(Agent_.id), root.get(Agent_.updatedAt))).orderBy(orders);

        List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
                .collect(Collectors.toList());

        return new PageImpl<>(briefs, pageable, total);
    }

    @Override
    public long countTotalAmount(Agent parent, int level) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Agent> countRoot = countQuery.from(Agent.class);

        List<Predicate> countPredicates = new ArrayList<>();

        Optional.ofNullable(parent).ifPresent(p -> countPredicates.add(builder.equal(countRoot.get(Agent_.parent), p)));
        if (level > 0) {
            countPredicates.add(builder.equal(countRoot.get(Agent_.level), level));
        } else {
            countPredicates.add(builder.gt(countRoot.get(Agent_.level), 0));
        }

        countQuery.select(builder.count(countRoot.get(Agent_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countNewAmount(Agent parent) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Agent> countRoot = countQuery.from(Agent.class);

        List<Predicate> countPredicates = new ArrayList<>();

        Optional.ofNullable(parent).ifPresent(p -> countPredicates.add(builder.equal(countRoot.get(Agent_.parent), p)));

        countPredicates.add(builder.greaterThanOrEqualTo(countRoot.get(Agent_.createdAt), DateUtils.currentDateStartTime()));

        countQuery.select(builder.count(countRoot.get(Agent_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }
}
