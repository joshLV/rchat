package com.rchat.platform.domain;

import com.rchat.platform.common.DateUtils;
import com.rchat.platform.common.RchatUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

public class TalkbackUserRepositoryImpl implements TalkbackUserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
	@Autowired
	private JdbcTemplate jdbcTemplate;

    @Override
    public Page<TalkbackUser> search(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated,
                                     Optional<Department> department, Optional<Group> group, Optional<Agent> agent, Optional<Date> createdStart,
                                     Optional<Date> createdEnd, Optional<Date> renewStart, Optional<Date> renewEnd, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> entityQuery = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> entityRoot = entityQuery.from(TalkbackUser.class);
//        Root<TalkbackNumber> talkbackNumber=entityQuery.from(TalkbackNumber.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> entityPredicates = new ArrayList<>();

        fullValue.ifPresent(v -> {
            Predicate predicate;
            v = "%" + v + "%";

            predicate = builder.like(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), v);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), v);
            entityPredicates.add(predicate);
        });
        
        shortValue.ifPresent(v -> {
            Predicate predicate;
            v = "%" + v + "%";

            predicate = builder.like(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.shortValue), v);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(TalkbackUser_.number).get(TalkbackNumber_.shortValue), v);
            entityPredicates.add(predicate);
        });

        role.ifPresent(r -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackUser_.role), r);
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackUser_.role), r);
            entityPredicates.add(predicate);

        });
        
        activated.ifPresent(a -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackUser_.activated), a);
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackUser_.activated), a);
            entityPredicates.add(predicate);

        });
        
        name.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(TalkbackUser_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(TalkbackUser_.name), n);
            entityPredicates.add(predicate);

        });

        department.ifPresent(d -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackUser_.department), d);
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackUser_.department), d);
            entityPredicates.add(predicate);
        });

        group.ifPresent(g -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackUser_.group), g);
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackUser_.group), g);
            entityPredicates.add(predicate);
        });

        agent.ifPresent(a -> {
            Predicate predicate;

            predicate = builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a);
            countPredicates.add(predicate);

            predicate = builder.equal(entityRoot.get(TalkbackUser_.group).get(Group_.agent), a);
            entityPredicates.add(predicate);
        });

        createdStart.ifPresent(d -> {
            Predicate predicate;

            predicate = builder.greaterThanOrEqualTo(countRoot.get(TalkbackUser_.createdAt), d);
            countPredicates.add(predicate);

            predicate = builder.greaterThanOrEqualTo(entityRoot.get(TalkbackUser_.createdAt), d);
            entityPredicates.add(predicate);
        });

        createdEnd.ifPresent(d -> {
            Predicate predicate;

            predicate = builder.lessThanOrEqualTo(countRoot.get(TalkbackUser_.createdAt), d);
            countPredicates.add(predicate);

            predicate = builder.lessThanOrEqualTo(entityRoot.get(TalkbackUser_.createdAt), d);
            entityPredicates.add(predicate);
        });

        renewStart.ifPresent(d -> {
            Predicate predicate;

            predicate = builder.greaterThanOrEqualTo(countRoot.get(TalkbackUser_.lastRenewed), d);
            countPredicates.add(predicate);

            predicate = builder.greaterThanOrEqualTo(entityRoot.get(TalkbackUser_.lastRenewed), d);
            entityPredicates.add(predicate);
        });

        renewEnd.ifPresent(d -> {
            Predicate predicate;

            predicate = builder.lessThanOrEqualTo(countRoot.get(TalkbackUser_.lastRenewed), d);
            countPredicates.add(predicate);

            predicate = builder.lessThanOrEqualTo(entityRoot.get(TalkbackUser_.lastRenewed), d);
            entityPredicates.add(predicate);
        });

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id)))
                .where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = new ArrayList<>();
//        orders.add(builder.asc(talkbackNumber));
        Optional.ofNullable(pageable.getSort()).ifPresent(s -> s.forEach(o -> {
            if (o.isAscending())
                orders.add(builder.asc(entityRoot.get(o.getProperty())));
            else
                orders.add(builder.desc(entityRoot.get(o.getProperty())));
        }));

        entityQuery.where(entityPredicates.toArray(new Predicate[0])).orderBy(orders);
        List<TalkbackUser> users = em.createQuery(entityQuery).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
//        Collections.sort(users, new Comparator<TalkbackUser>() {
//            public int compare(TalkbackUser arg0, TalkbackUser arg1) {
//            	Double hits0 =Double.valueOf(arg0.getNumber().getShortValue()) ;
//                Double hits1 = Double.valueOf(arg1.getNumber().getShortValue());
//                if (hits1 <hits0) {
//                    return 1;
//                } else if (hits1 == hits0) {
//                    return 0;
//                } else {
//                    return -1;
//                }
//            }
//        });

        return new PageImpl<>(users, pageable, total);
    }


    @Override
    public boolean recycle(List<TalkbackUser> users) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaUpdate<TalkbackUser> userUpdate = builder.createCriteriaUpdate(TalkbackUser.class);
        Root<TalkbackUser> userRoot = userUpdate.from(TalkbackUser.class);

        userUpdate.set(userRoot.get(TalkbackUser_.activated), Boolean.FALSE);
        userUpdate.set(userRoot.get(TalkbackUser_.department), builder.nullLiteral(Department.class));
        userUpdate.set(userRoot.get(TalkbackUser_.updatedAt), new Date());

        Set<String> ids = users.parallelStream().map(TalkbackUser::getId).collect(Collectors.toSet());
        Predicate predicate = userRoot.get(TalkbackUser_.id).in(ids);

        userUpdate.where(predicate);

        Query query = em.createQuery(userUpdate);
        return query.executeUpdate() == users.size();
    }

    @Override
    public boolean setEnabled(List<TalkbackUser> users, boolean enabled) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaUpdate<TalkbackUser> userUpdate = builder.createCriteriaUpdate(TalkbackUser.class);
        Root<TalkbackUser> userRoot = userUpdate.from(TalkbackUser.class);

        userUpdate.set(userRoot.get(TalkbackUser_.enabled), enabled);
        userUpdate.set(userRoot.get(TalkbackUser_.updatedAt), new Date());

        Set<String> ids = users.parallelStream().map(TalkbackUser::getId).collect(Collectors.toSet());
        Predicate predicate = userRoot.get(TalkbackUser_.id).in(ids);

        userUpdate.where(predicate);

        Query query = em.createQuery(userUpdate);
        return query.executeUpdate() == users.size();
    }

    @Override
    public Page<Brief> findBriefs(Pageable pageable) {
        return findBriefs(null, pageable);
    }

    @Override
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(server).ifPresent(s -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.server), s)));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(predicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(builder.tuple(root.get(TalkbackUser_.id), root.get(TalkbackUser_.updatedAt)))
                .orderBy(orders)
                .where(predicates.toArray(new Predicate[0]));

        List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
                .collect(Collectors.toList());

        return new PageImpl<>(briefs, pageable, total);
    }

    @Override
    public Page<TalkbackUser> statExpiredTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(groupName).ifPresent(n -> {
            String name = "%" + n + "%";

            countPredicates.add(builder.like(countRoot.get(TalkbackUser_.group).get(Group_.name), name));
            predicates.add(builder.like(root.get(TalkbackUser_.group).get(Group_.name), name));
        });

        Optional.ofNullable(agentName).ifPresent(n -> {
            String name = "%" + n + "%";

            countPredicates.add(builder.like(countRoot.get(TalkbackUser_.group).get(Group_.agent).get(Agent_.name), name));
            predicates.add(builder.like(root.get(TalkbackUser_.group).get(Group_.agent).get(Agent_.name), name));
        });
        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });

        Date now = new Date();

        countPredicates.add(builder.lessThan(countRoot.get(TalkbackUser_.deadline), now));
        predicates.add(builder.lessThan(countRoot.get(TalkbackUser_.deadline), now));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);

        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUser> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<TalkbackUser> statExpiredTalkbackUsers(Agent agent, Group group, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a));
            predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a));
        });

        Optional.ofNullable(group).ifPresent(g -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g));
            predicates.add(builder.equal(root.get(TalkbackUser_.group), g));
        });

        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });

        Date now = new Date();

        countPredicates.add(builder.lessThan(countRoot.get(TalkbackUser_.deadline), now));
        predicates.add(builder.lessThan(countRoot.get(TalkbackUser_.deadline), now));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUser> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<TalkbackUser> statExpiringTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(groupName).ifPresent(n -> {
            String name = "%" + n + "%";

            countPredicates.add(builder.like(countRoot.get(TalkbackUser_.group).get(Group_.name), name));
            predicates.add(builder.like(root.get(TalkbackUser_.group).get(Group_.name), name));
        });

        Optional.ofNullable(agentName).ifPresent(n -> {
            String name = "%" + n + "%";

            countPredicates.add(builder.like(countRoot.get(TalkbackUser_.group).get(Group_.agent).get(Agent_.name), name));
            predicates.add(builder.like(root.get(TalkbackUser_.group).get(Group_.agent).get(Agent_.name), name));
        });
        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });

        // 得到当前时间
        Calendar start = Calendar.getInstance();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 1);

        countPredicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));
        predicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);

        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUser> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<TalkbackUser> statExpiringTalkbackUsers(Agent agent, Group group, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a));
            predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a));
        });

        Optional.ofNullable(group).ifPresent(g -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), group));
            predicates.add(builder.equal(root.get(TalkbackUser_.group), group));
        });

        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });
        // 得到当前时间
        Calendar start = Calendar.getInstance();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 1);

        countPredicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));
        predicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUser> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }
    
    @Override
    public Page<TalkbackUser> statActivatedTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(groupName).ifPresent(n -> {
            String name = "%" + n + "%";

            countPredicates.add(builder.like(countRoot.get(TalkbackUser_.group).get(Group_.name), name));
            predicates.add(builder.like(root.get(TalkbackUser_.group).get(Group_.name), name));
        });

        Optional.ofNullable(agentName).ifPresent(n -> {
            String name = "%" + n + "%";

            countPredicates.add(builder.like(countRoot.get(TalkbackUser_.group).get(Group_.agent).get(Agent_.name), name));
            predicates.add(builder.like(root.get(TalkbackUser_.group).get(Group_.agent).get(Agent_.name), name));
        });
        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);

        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUser> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<TalkbackUser> statActivatedTalkbackUsers(Agent agent, Group group, String fullValue, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);

        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a));
            predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a));
        });

        Optional.ofNullable(group).ifPresent(g -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), group));
            predicates.add(builder.equal(root.get(TalkbackUser_.group), group));
        });

        Optional.ofNullable(fullValue).ifPresent(n -> {
            countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
            predicates.add(builder.equal(root.get(TalkbackUser_.number).get(TalkbackNumber_.fullValue), n));
        });
        // 得到当前时间
//        Calendar start = Calendar.getInstance();
//
//        Calendar end = Calendar.getInstance();
//        end.add(Calendar.MONTH, 1);
//
//        countPredicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));
//        predicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(root).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<TalkbackUser> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public long countExpiredAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        // 得到当前时间
        Calendar now = Calendar.getInstance();

        countPredicates.add(builder.lessThan(countRoot.get(TalkbackUser_.deadline), now.getTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countTotalAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();

    }

    @Override
    public List<TalkbackUser> findAll(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> countPredicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> countPredicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        query.where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public long countNewExpiredAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        countPredicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), DateUtils.currentDateStartTime(), DateUtils.currentDateEndTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countExpiringAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> countPredicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> countPredicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        // 得到当前时间
        Calendar start = Calendar.getInstance();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 1);

        countPredicates.add(builder.between(countRoot.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(countPredicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public List<TalkbackUser> findExpiring(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        // 得到当前时间
        Calendar start = Calendar.getInstance();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 1);

        predicates.add(builder.between(root.get(TalkbackUser_.deadline), start.getTime(), end.getTime()));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<TalkbackUser> findExpired(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        predicates.add(builder.lessThan(root.get(TalkbackUser_.deadline), new Date()));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<TalkbackUser> findNew(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        predicates.add(builder.greaterThan(root.get(TalkbackUser_.createdAt), DateUtils.currentDateStartTime()));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<TalkbackUser> findNonactivated(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.FALSE));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }
    
    @Override
    public List<TalkbackUser> findActivated(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<TalkbackUser> findNewActivated(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        predicates.add(builder.greaterThan(root.get(TalkbackUser_.activatedAt), DateUtils.currentDateStartTime()));
        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<TalkbackUser> findNewExpired(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<TalkbackUser> query = builder.createQuery(TalkbackUser.class);
        Root<TalkbackUser> root = query.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get(TalkbackUser_.activated), Boolean.TRUE));

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(root.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(root.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(root.get(TalkbackUser_.department), d)));

        predicates.add(builder.between(root.get(TalkbackUser_.deadline), DateUtils.currentDateStartTime(), DateUtils.currentDateEndTime()));

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override
    public long countNewAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        predicates.add(builder.greaterThanOrEqualTo(countRoot.get(TalkbackUser_.createdAt), DateUtils.currentDateStartTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countNonactivatedAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        // 未激活的
        predicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.FALSE));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countNewActivatedAmount(Agent agent, Group group, Department department) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<TalkbackUser> countRoot = countQuery.from(TalkbackUser.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(agent).ifPresent(a -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group).get(Group_.agent), a)));
        Optional.ofNullable(group).ifPresent(g -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.group), g)));
        Optional.ofNullable(department).ifPresent(d -> predicates.add(builder.equal(countRoot.get(TalkbackUser_.department), d)));

        predicates.add(builder.equal(countRoot.get(TalkbackUser_.activated), Boolean.TRUE));
        predicates.add(builder.greaterThan(countRoot.get(TalkbackUser_.activatedAt), DateUtils.currentDateStartTime()));

        countQuery.select(builder.count(countRoot.get(TalkbackUser_.id))).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

	@Override
	public  Page<TalkbackUser> page(Optional<String> fullValue, Optional<TalkbackRole> role,
			Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated,
			Optional<String> departmentId, Optional<String> groupId, Optional<String> agentId,
			Optional<Date> createdStart, Optional<Date> createdEnd, Optional<Date> renewStart, Optional<Date> renewEnd,
			Pageable pageable) {

    	String sql="select    ttn.full_value,"
			    			+ "ttn.short_value,"
			    			+ "tt.role,"
			    			+ "tt.name as user_name, "
			    			+ "ta.name as agent_name,"
			    			+ "tg.name as group_name ,"
			    			+ "td.name as department_name,"
			    			+ "tt.activated,"
			    			+ "tt.deadline,"
			    			+ "ta.id as agent_id,"
			    			+ "tg.id as group_id,"
			    			+ "td.id as department_id,"
			    			+ "tt.id,"
			    			+ "ttn.id as numberId"
    			    + " from t_agent ta "
			    			+ " LEFT JOIN t_group tg on tg.agent_id=ta.id "
			    			+ " LEFT JOIN t_department td on td.group_id=tg.id "
			    			+ " LEFT JOIN t_talkback_user tt on tt.group_id=tg.id "
			    			+ " LEFT JOIN t_talkback_number ttn on ttn.id=tt.talkback_number_id "+
			         " where "+
			              "  ta.id='"+agentId.get()+"'"+
			              "  and tg.id='"+groupId.get()+"'"+
			              "  and tt.group_id='"+groupId.get()+"'";
    	StringBuffer sb=new StringBuffer();
    	sb.append(sql);
		String lastSql= "  ORDER BY ttn.full_value asc limit "+pageable.getOffset() +","+pageable.getPageSize();
		sb.append(lastSql);
    	List<TalkbackUserVo> users=jdbcTemplate.query(sb.toString(), new TalkbackUserVo());
    	List<TalkbackUser> talkbackUsers=new ArrayList<TalkbackUser>();
    	for (TalkbackUserVo talkbackUserVo : users) {
    		TalkbackUser talkbackUser=new TalkbackUser();
    		TalkbackNumber talkbackNumber =new TalkbackNumber();
    		talkbackNumber.setId(talkbackUserVo.getNumberId());
    		talkbackNumber.setFullValue(talkbackUserVo.getFull_value());
    		talkbackNumber.setShortValue(talkbackUserVo.getShort_value());
    		talkbackUser.setNumber(talkbackNumber);
    		talkbackUser.setName(talkbackUserVo.getUser_name());
    		talkbackUser.setActivated(Boolean.getBoolean(talkbackUserVo.getActivated()));
    		Group group  =new Group();
    		Agent agent=new Agent();
    		agent.setId(talkbackUserVo.getAgent_id());
    		agent.setName(talkbackUserVo.getAgent_name());
    		group.setId(talkbackUserVo.getGroup_id());
    		group.setName(talkbackUserVo.getGroup_name());
    		talkbackUser.setGroup(group);
    		Department department=new Department();
    		department.setId(talkbackUserVo.getDepartment_id());
    		department.setName(talkbackUserVo.getDepartment_name());
    		talkbackUser.setDepartment(department);
    		talkbackUser.setDeadline(talkbackUserVo.getDeadline());
    		talkbackUsers.add(talkbackUser);
    		 
//    		talkbackUser.setRole(role);
//    		talkbackUser.setType(1);
			
		}

        return new PageImpl<>(talkbackUsers, pageable, talkbackUsers.size());
	}
}
