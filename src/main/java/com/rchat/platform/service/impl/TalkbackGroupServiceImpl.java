package com.rchat.platform.service.impl;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.service.TalkbackUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 对讲群组业务
 *
 * @author dzhang
 */
@Service
@SecurityService(ResourceType.TALKBACK_GROUP)
public class TalkbackGroupServiceImpl extends AbstractService<TalkbackGroup, String> implements TalkbackGroupService {
    @Autowired
    private TalkbackGroupRepository repository;
    @Autowired
    private TalkbackUserGroupRepository talkbackUserGroupRepository;
    @Autowired
    private TalkbackUserService talkbackUserService;

    @Override
    protected JpaRepository<TalkbackGroup, String> repository() {
        return repository;
    }

    private long getUserCount(TalkbackGroup group) {
        if (group == null) {
            return 0;
        }

        return talkbackUserService.countByTalkbackGroup(group);
    }
    @SecurityMethod(false)
    @Override
    public List<TalkbackGroup> findAll() {
        List<TalkbackGroup> groups = super.findAll();
        groups.forEach(g -> g.setUserCount(getUserCount(g)));
        return groups;
    }
    @SecurityMethod(false)
    @Override
    public Page<TalkbackGroup> findAll(Pageable pageable) {
        Page<TalkbackGroup> page = super.findAll(pageable);
        page.forEach(g -> g.setUserCount(getUserCount(g)));

        return page;
    }
    @SecurityMethod(false)
    @Override
    @Transactional
    public List<TalkbackGroup> create(List<TalkbackGroup> groups) {
        groups = super.create(groups);
        groups.parallelStream().forEach(group -> saveTalkbackUserGroups(group));

        return groups;
    }
    @SecurityMethod(false)
    @Override
    @Transactional
    public TalkbackGroup create(TalkbackGroup group) {
    	group.setGroupOnlyId(this.getNumber());
        group = super.create(group);
        saveTalkbackUserGroups(group);

        return group;
    }
    /**
  	 * 生成16位唯一的数字
  	 */
  	public Integer getNumber(){
  	        //随机生成一位整数
  	        //生成uuid的hashCode值
  	        int hashCode = UUID.randomUUID().toString().hashCode();
  	        //可能为负数
  	        if(hashCode<0){
  	            hashCode = -hashCode;
  	        }
  	        TalkbackGroup old= this.findNum(hashCode);
  	        if(ToolsUtil.isNotEmpty(old)){
  	        	this.getNumber();
  	        }
  	        return hashCode;
      }
  	@SecurityMethod(false)
    @Override
    @Transactional
    public TalkbackGroup update(TalkbackGroup group) {
        saveTalkbackUserGroups(group);

        return super.update(group);
    }

    private void saveTalkbackUserGroups(TalkbackGroup group) {
        List<TalkbackUser> users = Optional.ofNullable(group.getUsers()).orElse(Collections.emptyList());
        List<TalkbackUserGroup> existsTalkbackUserGroups = talkbackUserGroupRepository.findByGroupId(group.getId());

        List<TalkbackUserGroup> talkbackUserGroups = users.parallelStream().map(u -> new TalkbackUserGroup(u, group))
                .collect(Collectors.toList());

        // 保留还包含的关系
        List<TalkbackUserGroup> deletedUserGroups = existsTalkbackUserGroups.parallelStream()
                .filter(ug -> !talkbackUserGroups.contains(ug)).collect(Collectors.toList());

        // 增加新新的关系
        List<TalkbackUserGroup> newUserGroups = talkbackUserGroups.parallelStream()
                .filter(ug -> !existsTalkbackUserGroups.contains(ug)).collect(Collectors.toList());

        talkbackUserGroupRepository.save(newUserGroups);
        talkbackUserGroupRepository.delete(deletedUserGroups);
        talkbackUserGroupRepository.flush();
    }
    @SecurityMethod(false)
    @Override
    public Optional<TalkbackGroup> findByDepartmentAndType(Department department, TalkbackGroupType type) {
        Optional<TalkbackGroup> o = repository.findByDepartmentAndType(department, type);
        o.ifPresent(g -> g.setUserCount(getUserCount(g)));
        return o;
    }

    @Override
    public Page<TalkbackGroup> findByGroup(Group group, Pageable pageable) {
        Page<TalkbackGroup> page = repository.findByGroup(group, pageable);
        page.forEach(g -> g.setUserCount(getUserCount(g)));
        return page;
    }
    @SecurityMethod(false)
    @Override
    @Transactional
    public void setTalbackUsers(TalkbackGroup group) {
        saveTalkbackUserGroups(group);
    }

    @Override
    public Page<TalkbackGroup> findByDepartments(List<Department> departments, Pageable pageable) {
        Page<TalkbackGroup> page = repository.findByDepartmentIn(departments, pageable);
        page.forEach(g -> g.setUserCount(getUserCount(g)));
        return page;
    }

    @Override
    public Page<TalkbackGroup> findByDepartment(Department department, Pageable pageable) {
        Page<TalkbackGroup> page = repository.findByDepartment(department, pageable);
        page.forEach(g -> g.setUserCount(getUserCount(g)));
        return page;
    }

    @Override
    public Page<TalkbackGroup> search(Optional<Group> group, Optional<Department> department, Optional<String> name,
                                      Pageable pageable) {
        Page<TalkbackGroup> page = repository.search(group, department, name, pageable);
        page.forEach(g -> g.setUserCount(getUserCount(g)));
        return page;
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        return repository.findBriefs(server, pageable);
    }

	@Override
	public TalkbackGroup findNum(int hashCode) {
		return repository.findNum(hashCode);
	}
}
