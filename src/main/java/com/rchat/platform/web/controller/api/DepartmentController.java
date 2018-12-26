package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.DepartmentNotFoundException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.exception.IdNotMatchException;
import com.rchat.platform.web.exception.LevelDeepException;
import com.rchat.platform.web.exception.TalkbackGroupNotFoundException;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentViewService departmentViewService;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private TalkbackGroupService talkbackGroupService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private RchatEnv env;
    @Autowired
    private SummaryService summaryService;

    @Autowired
    private JmsTemplate jms;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建部门")
    public Department create(@Validated @RequestBody Department department) {
        department.setCreator(RchatUtils.currentUser());

        Group group = groupService.findOne(department.getGroup().getId()).orElseThrow(GroupNotFoundException::new);
        department.setGroup(group);

        // 默认认为部门的level == 1
        // 如果指定的父级部门，level就不能是1了，而是父级部门的level + 1
        Optional.ofNullable(department.getParent()).ifPresent(p -> {
            Department parent = departmentService.findOne(p.getId()).orElseThrow(DepartmentNotFoundException::new);

            int level = parent.getLevel() + 1;

            // 最多 10 级部门
            if (level > 10) {
                throw new LevelDeepException();
            }

            department.setParent(parent);
            department.setLevel(level);
        });

        Department d = departmentService.create(department);
        jms.convertAndSend(TopicNameConstants.DEPARTMENT_CREATE, d);
        jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_CREATE, d.getDefaultGroup());

        return d;
    }

    @DeleteMapping("/{id}")
    @LogAPI("删除部门")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除部门成功")
    public void delete(@PathVariable String id) {
        Optional<Department> o = findOne(id);
        Department department = o.orElseThrow(DepartmentNotFoundException::new);
        //需求变更: 当部门只剩下默认群组,并且对讲账户为0的时候,可以同时删除部门和对讲群组
        TalkbackGroup talkbackGroup = talkbackGroupService.findByDepartmentAndType(department, TalkbackGroupType.DEFAULT).orElseThrow(TalkbackGroupNotFoundException::new);
        Summary summary = summaryService.count(department);
        long amount = summary.getUserAmount();
        if(amount == 0){
        	talkbackGroupService.delete(talkbackGroup);
        	departmentService.delete(department);
        	jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_DELETE, talkbackGroup);
            jms.convertAndSend(TopicNameConstants.DEPARTMENT_DELETE, department);
        } else {        	
        	departmentService.delete(department);
        }
    }

    @PutMapping("/{id}")
    @LogAPI("更新部门信息")
    public Department update(@PathVariable String id, @RequestBody Department department) {
        if (!id.equals(department.getId())) {
            throw new IdNotMatchException();
        }

        department = departmentService.update(department);
        jms.convertAndSend(TopicNameConstants.DEPARTMENT_UPDATE, department);

        return department;
    }

    @GetMapping("/{id}")
    public Department detail(@PathVariable String id) {
        Optional<Department> o = findOne(id);
        Department department = o.orElseThrow(DepartmentNotFoundException::new);
        department.setParent(new Department());

        setSummary(department);

        return department;
    }

    @GetMapping("/{id}/parent")
    public Department parent(@PathVariable String id) {
        Optional<Department> parent = departmentService.findParent(new Department(id));
        return parent.orElseGet(Department::new);
    }

    private void setSummary(Department department) {
        if (department == null) {
            return;
        }

        Summary summary = summaryService.count(department);
        department.setUserAmount(summary.getUserAmount());
        department.setExpiredUserAmount(summary.getExpiredUserAmount());
        department.setExpiringUserAmount(summary.getExpiringUserAmount());
        department.setNonactiveUserAmount(summary.getNonactiveUserAmount());

        List<Department> children = department.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(this::setSummary);
        }
    }

    private Optional<Department> findOne(String id) {
        Optional<Department> o;

        if (RchatUtils.isGroupAdmin()) {
            Group group = env.currentGroup();
            o = departmentService.findByGroupAndId(group, id);
        } else if (RchatUtils.isDepartmentAdmin()) {
            Department parent = env.currentDepartment();
            if (parent.getId().equals(id)) {
                return Optional.of(parent);
            }

            o = departmentViewService.findOneUnderDepartment(id, parent);
            o.ifPresent(d -> d.setParent(parent));
        } else if (RchatUtils.isRchatAdmin()) {
            o = departmentService.findOne(id);
        } else {
            o = Optional.empty();
        }
        return o;
    }

    @GetMapping(path = "/{id}", params = "depth")
    public Department tree(@PathVariable String id, @RequestParam Integer depth) {
        Optional<Department> o = findOne(id);
        Department parent = o.orElseThrow(DepartmentNotFoundException::new);

        // 如果是集团管理员查询， 则先查出此集团指定部门下的直接子部门
        // 然后不全其子部门
        parent.setChildren(departmentService.findDepartmentTree(parent, depth - 1));
        
        parent.setParent(new Department());

        return parent;
    }

    @GetMapping
    public List<Department> tree(@RequestParam(defaultValue = "3") int depth) {
        if (RchatUtils.isGroupAdmin()) {
            Group group = env.currentGroup();
            List<Department> departments = departmentService.findByGroupDirectlyUnder(group);

            departments.parallelStream()
                    .forEach(d -> d.setChildren(departmentService.findDepartmentTree(d, depth - 1)));

            return departments;
        } else if (RchatUtils.isDepartmentAdmin()) {
            Department department = env.currentDepartment();
            department.setChildren(departmentService.findDepartmentTree(department, depth - 1));

            return Collections.singletonList(department);
        } else {
            throw new NoRightAccessException();
        }
    }

    @GetMapping("/{departmentId}/talkback-users")
    public Page<TalkbackUser> talbackUsers(@PathVariable String departmentId, @PageableDefault Pageable pageable) {
        Optional<Department> o = findOne(departmentId);
        Department department = o.orElseThrow(DepartmentNotFoundException::new);

        List<Department> departments = Collections.singletonList(department);
        return talkbackUserService.findByDepartments(departments, pageable);
    }

    @GetMapping("/{departmentId}/talkback-groups")
    public Page<TalkbackGroup> talbackGroups(@PathVariable String departmentId, @PageableDefault Pageable pageable) {
        Optional<Department> o = findOne(departmentId);
        Department department = o.orElseThrow(DepartmentNotFoundException::new);

        return talkbackGroupService.findByDepartment(department, pageable);
    }
}
