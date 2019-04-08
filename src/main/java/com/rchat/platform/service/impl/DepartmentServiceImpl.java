package com.rchat.platform.service.impl;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@SecurityService(ResourceType.DEPARTMENT)
public class DepartmentServiceImpl extends AbstractService<Department, String> implements DepartmentService {
    @Autowired
    private DepartmentRepository repository;
    @Autowired
    private DepartmentParentService departmentParentService;
    @Autowired
    private UserService userService;
    @Autowired
    private TalkbackGroupService talkbackGroupService;
    @Autowired
    private RchatEnv env;
    @Autowired
    private SummaryService summaryService;

    @Override
    protected JpaRepository<Department, String> repository() {
        return repository;
    }
    @SecurityMethod(false)
    @Override
    public Page<Department> search(Group group, Optional<String> departmentName, Optional<String> adminName,
                                   Optional<String> adminUsername, Pageable pageable) {
        Page<Department> departments = repository.search(group, departmentName, adminName, adminUsername, pageable);
        departments.forEach(department -> {
            Summary summary = summaryService.count(department);
            department.setUserAmount(summary.getUserAmount());
            department.setNonactiveUserAmount(summary.getNonactiveUserAmount());
            department.setExpiredUserAmount(summary.getExpiredUserAmount());
            department.setExpiringUserAmount(summary.getExpiringUserAmount());
        });
        return departments;
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        return repository.findBriefs(server, pageable);
    }
    @SecurityMethod(false)
    @Override
    @Transactional
    public Department update(Department entity) {
        userService.update(entity.getAdministrator());

        return repository.saveAndFlush(entity);
    }
    @SecurityMethod(false)
    @Override
    @Transactional
    public Department create(Department department) {
        User user = department.getAdministrator();
        Role role = null;

        if (department.getParent() == null) {
            // 公司管理员
            role = new Role("e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0");
        } else {
            // 部门管理员
            role = new Role("219e8e4d-f32b-4434-b045-734eb9e9f80a");
        }

        user.setRoles(Arrays.asList(role));

        department.setAdministrator(userService.create(user));

        department = super.create(department);

        // 创建默认群组
        TalkbackGroup talkbackGroup = new TalkbackGroup();
        talkbackGroup.setType(TalkbackGroupType.DEFAULT);
        talkbackGroup.setName(department.getName());
        talkbackGroup.setGroup(department.getGroup());
        talkbackGroup.setDepartment(department);

        talkbackGroupService.create(talkbackGroup);

        // 保存所有部门的父级部门
        String departmentId = department.getId();
        Optional.ofNullable(department.getParent()).ifPresent(parent -> {
            // 获取父级部门的全部父级部门
            List<DepartmentParent> parentParents = departmentParentService.findParents(parent);

            List<DepartmentParent> parents = new ArrayList<>();
            parentParents.forEach(p -> parents.add(new DepartmentParent(departmentId, p.getParentId(), p.getLevel())));

            String parentId = parent.getId();
            parents.add(new DepartmentParent(departmentId, parentId, parent.getLevel()));

            departmentParentService.create(parents);
        });

        department.setDefaultGroup(talkbackGroup);
        return department;
    }
    @SecurityMethod(false)
    @Override
    public Optional<Department> findOne(String id) {
        Optional<Department> o = super.findOne(id);

        o.ifPresent(d -> d.setParent(repository.findParent(d).orElse(null)));

        return o;
    }

    @Override
    public Optional<Department> findParent(Department child) {
        return repository.findParent(child);
    }

    @Override
    public List<Department> findDepartmentTree(Department department, int depth) {
        if (depth <= 0) {
            return Collections.emptyList();
        }
        --depth;

        List<Department> parents = new ArrayList<>();
        // 如果部门为空，说明是集团管理员在查询部门信息
        if (department == null) {
            Group group = env.currentGroup();
            // 一级部门，集团直属
            parents.addAll(repository.findByGroupAndParentIsNull(group));
        } else {
            // 用于延迟加载
            parents.addAll(repository.findByParent(department));
        }

        for (Department parent : parents) {
            List<Department> children = findDepartmentTree(parent, depth);
            parent.setChildren(children);
        }

        return parents;
    }

    @Override
    public List<Department> findByGroup(Group group) {
        return repository.findByGroup(group);
    }

    @Override
    public List<Department> findByGroupDirectlyUnder(Group group) {
        return repository.findByGroupAndParentIsNull(group);
    }

    @Override
    @Cacheable(cacheNames = "rchat", key = "#root.args[0].id")
    public Optional<Department> findByAdministrator(User user) {
        return repository.findByAdministrator(user);
    }

    @Override
    public List<Department> findByGroupAndParent(Group group, Department parent) {
        return repository.findByGroupAndParent(group, parent);
    }

    @Override
    public Optional<Department> findByGroupAndId(Group group, String id) {
        return repository.findByGroupAndId(group, id);
    }
}