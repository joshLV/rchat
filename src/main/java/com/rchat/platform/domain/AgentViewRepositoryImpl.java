package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AgentViewRepositoryImpl implements AgentViewRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<AgentView> search(Agent parent, Optional<String> name, Optional<String> adminName,
                                  Optional<String> adminUsername, Pageable pageable) {

        CriteriaBuilder builder = em.getCriteriaBuilder();

        // 先查总数
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<AgentView> agentQuery = builder.createQuery(AgentView.class);

        Root<AgentView> countRoot = countQuery.from(AgentView.class);
        Root<AgentView> agentRoot = agentQuery.from(AgentView.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> agentPredicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(AgentView_.viewId).get(AgentViewId_.parentId), parent.getId()));
        agentPredicates.add(builder.equal(agentRoot.get(AgentView_.viewId).get(AgentViewId_.parentId), parent.getId()));

        name.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(AgentView_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(agentRoot.get(AgentView_.name), n);
            agentPredicates.add(predicate);
        });

        adminName.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(AgentView_.administrator).get(User_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(agentRoot.get(AgentView_.administrator).get(User_.name), n);
            agentPredicates.add(predicate);
        });

        adminUsername.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(AgentView_.administrator).get(User_.username), n);
            countPredicates.add(predicate);

            predicate = builder.like(agentRoot.get(AgentView_.administrator).get(User_.username), n);
            agentPredicates.add(predicate);
        });

        countQuery.select(builder.count(countRoot.get(AgentView_.viewId).get(AgentViewId_.id)))
                .where(countPredicates.toArray(new Predicate[0]));
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

        List<AgentView> agents = em.createQuery(agentQuery).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(agents, pageable, total);
    }
}
