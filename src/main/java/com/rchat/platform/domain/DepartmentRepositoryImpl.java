package com.rchat.platform.domain;


import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.DepartmentPrivilege.OverLevelCallType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DepartmentRepositoryImpl implements DepartmentRepositoryCustom {

    private static Department extract(ResultSet rs) throws SQLException {
        Department department = new Department();

        department.setId(rs.getString("id"));
        department.setName(rs.getString("name"));
        department.setLinkman(rs.getString("linkman"));
        department.setEmail(rs.getString("email"));
        department.setPhone(rs.getString("phone"));

        DepartmentPrivilege privilege = new DepartmentPrivilege();

        privilege.setGpsEnabled(rs.getBoolean("gps_enabled"));
        privilege.setOverGroupEnabled(rs.getBoolean("over_group_enabled"));

        int type = rs.getInt("over_level_call_type");
        for (OverLevelCallType t : OverLevelCallType.values()) {
            if (type == t.ordinal()) {
                privilege.setOverLevelCallType(t);
                break;
            }
        }

        department.setPrivilege(privilege);

        Group group = new Group();
        group.setId(rs.getString("group_id"));

        department.setGroup(group);

        User administrator = new User();
        administrator.setId(rs.getString("administrator"));

        department.setAdministrator(administrator);

        User creator = new User();
        creator.setId(rs.getString("creator"));

        department.setCreator(creator);

        Optional<String> parentId = Optional.ofNullable(rs.getString("parent"));
        parentId.ifPresent(id -> {
            Department parent = new Department(id);

            department.setParent(parent);
        });
        return department;
    }

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private JdbcTemplate jdbc;

    //
    private static class DepartmentResultSetExtractor implements ResultSetExtractor<Department> {

        @Override
        public Department extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }
            return extract(rs);
        }
    }

    @Override
    public Page<Department> search(Group group, Optional<String> name, Optional<String> aName,
                                   Optional<String> aUsername, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        CriteriaQuery<Department> entityQuery = builder.createQuery(Department.class);

        Root<Department> countRoot = countQuery.from(Department.class);
        Root<Department> entityRoot = entityQuery.from(Department.class);

        List<Predicate> countPredicates = new ArrayList<>();
        List<Predicate> entityPredicates = new ArrayList<>();

        countPredicates.add(builder.equal(countRoot.get(Department_.group), group));
        entityPredicates.add(builder.equal(entityRoot.get(Department_.group), group));

        name.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(Department_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(Department_.name), n);
            entityPredicates.add(predicate);
        });

        aName.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(Department_.administrator).get(User_.name), n);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(Department_.administrator).get(User_.name), n);
            entityPredicates.add(predicate);
        });

        aUsername.ifPresent(n -> {
            Predicate predicate;
            n = "%" + n + "%";

            predicate = builder.like(countRoot.get(Department_.administrator).get(User_.username), n);
            countPredicates.add(predicate);

            predicate = builder.like(entityRoot.get(Department_.administrator).get(User_.username), n);
            entityPredicates.add(predicate);
        });

        countQuery.select(builder.count(countRoot.get(Department_.id)))
                .where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, entityRoot);

        entityQuery.where(entityPredicates.toArray(new Predicate[0])).orderBy(orders);

        List<Department> departments = em.createQuery(entityQuery).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        departments.parallelStream().forEach(d -> {
            em.detach(d);
            d.setParent(null);
        });

        return new PageImpl<>(departments, pageable, total);
    }

    @Override
    public Optional<Department> findParent(Department department) {
        Object[] args = {department.getId()};
        String sql = "SELECT * FROM t_department d WHERE d.id IN (SELECT c.parent FROM t_department c WHERE c.id = ?)";

        return Optional.ofNullable(jdbc.query(sql, args, new DepartmentResultSetExtractor()));
    }

    @Override
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Department> countRoot = countQuery.from(Department.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(server).ifPresent(s -> predicates.add(builder.equal(countRoot.get(Department_.group).get(Group_.server), s)));

        countQuery.select(builder.count(countRoot.get(Department_.id))).where(predicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<Department> root = query.from(Department.class);

        List<Order> orders = RchatUtils.sort2Orders(pageable.getSort(), builder, root);
        query.select(builder.tuple(root.get(Department_.id), root.get(Department_.updatedAt))).orderBy(orders).where(predicates.toArray(new Predicate[0]));

        List<Tuple> list = em.createQuery(query).setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        List<Brief> briefs = list.parallelStream().map(t -> new Brief(t.get(0, String.class), t.get(1, Date.class)))
                .collect(Collectors.toList());

        return new PageImpl<>(briefs, pageable, total);
    }
}
