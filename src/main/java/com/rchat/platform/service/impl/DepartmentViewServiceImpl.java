package com.rchat.platform.service.impl;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.DepartmentViewService;
import com.rchat.platform.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentViewServiceImpl extends AbstractService<DepartmentView, DepartmentViewId>
        implements DepartmentViewService {
    @Autowired
    private DepartmentViewRepository repository;
    @Autowired
    private SummaryService summaryService;

    @Override
    protected JpaRepository<DepartmentView, DepartmentViewId> repository() {
        return repository;
    }

    @Override
    public Page<Department> findChildren(Department parent, Pageable pageable) {
        Page<DepartmentView> page = repository.findByViewIdParentId(parent.getId(), pageable);
        return viewPage2DepartmentPage(page, pageable);
    }

    private Page<Department> viewPage2DepartmentPage(Page<DepartmentView> page, Pageable pageable) {
        Page<Department> departments = page.map(RchatUtils::view2Entity);
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
    public Page<Department> search(Group group, Department parent, Optional<String> name, Optional<String> aName,
                                   Optional<String> aUsername, Pageable pageable) {
        Page<DepartmentView> page = repository.search(group, parent, name, aName, aUsername, pageable);
        return viewPage2DepartmentPage(page, pageable);
    }

    @Override
    public Optional<Department> findOneUnderDepartment(String id, Department parent) {
        Optional<DepartmentView> view = repository.findByViewId(new DepartmentViewId(id, parent.getId()));
        return view.map(RchatUtils::view2Entity);
    }

    @Override
    public List<Department> findChildren(Department parent) {
        List<DepartmentView> views = repository.findByViewIdParentId(parent.getId());
        return views.parallelStream().map(RchatUtils::view2Entity).collect(Collectors.toList());
    }

}
