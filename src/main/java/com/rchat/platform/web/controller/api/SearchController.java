package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.format.DomainTypeFormatter;
import com.rchat.platform.web.format.TalkbackRoleFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private AgentViewService agentViewService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentViewService departmentViewService;
    @Autowired
    private TalkbackGroupService talkbackGroupService;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private SummaryService summaryService;
    @Autowired
    private RchatEnv env;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new DomainTypeFormatter());
        binder.addCustomFormatter(new TalkbackRoleFormatter());
    }

    @GetMapping("/agents")
    public Page<Agent> agents(@RequestParam Optional<String> agentName, @RequestParam Optional<String> adminName,
                              @RequestParam Optional<String> adminUsername, @PageableDefault Pageable pageable) {
        Agent parent = env.currentAgent();
        return agentViewService.search(parent, agentName, adminName, adminUsername, pageable);
    }

    @GetMapping("/groups")
    public Page<? extends Serializable> groups(@RequestParam Optional<String> agentId,
                                               @RequestParam Optional<String> groupName,
                                               @RequestParam Optional<String> adminName,
                                               @RequestParam Optional<String> adminUsername,
                                               @RequestParam Optional<Boolean> status,
                                               @RequestParam(required = false) Boolean withStastic,
                                               @PageableDefault Pageable pageable) {

        if (RchatUtils.isAgentAdmin()) {
            agentId = Optional.ofNullable(env.currentAgent().getId());
        }
        Page<Group> groupPage = groupService.search(agentId, groupName, adminName, adminUsername, status, pageable);
        if (withStastic != null) {
            return groupPage.map(this::convertToStastic);
        } else {
            groupPage.forEach(this::setSummary);
            return groupPage;
        }
    }

    private void setSummary(Group group) {
        Summary summary = summaryService.count(group);
        group.setCreditRemaint(summary.getCreditRemaint());
        group.setCreditAccumulation(summary.getCreditAccumulation());
        group.setUserAmount(summary.getUserAmount());
        group.setExpiringUserAmount(summary.getExpiringUserAmount());
        group.setExpiredUserAmount(summary.getExpiredUserAmount());
        group.setNonactiveUserAmount(summary.getNonactiveUserAmount());
    }

    private GroupStastic convertToStastic(Group group) {
        GroupStastic s = new GroupStastic();
        s.setId(group.getId());
        s.setName(group.getName());
        s.setServer(group.getServer());
        s.setAgentId(group.getAgent().getId());
        s.setAgentName(group.getAgent().getName());
        s.setTalkbackUserCount(talkbackUserService.countByGroup(group));

        Summary summary = summaryService.count(group);

        s.setUserAmount(summary.getUserAmount());
        s.setNonactiveUserAmount(summary.getNonactiveUserAmount());
        s.setExpiredUserAmount(summary.getExpiredUserAmount());
        s.setExpiringUserAmount(summary.getExpiringUserAmount());
        s.setCreditAccumulation(summary.getCreditAccumulation());
        s.setCreditRemaint(summary.getCreditRemaint());
        return s;
    }

    static class GroupStastic implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private Server server;
        private String agentId;
        private String agentName;
        private Long talkbackUserCount;
        private Long userAmount;
        private Long nonactiveUserAmount;
        private Long expiredUserAmount;
        private Long expiringUserAmount;
        private Long creditRemaint;
        private Long creditAccumulation;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Server getServer() {
            return server;
        }

        public void setServer(Server server) {
            this.server = server;
        }

        public Long getTalkbackUserCount() {
            return talkbackUserCount;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public void setTalkbackUserCount(Long talkbackUserCount) {
            this.talkbackUserCount = talkbackUserCount;
        }

        public Long getUserAmount() {
            return userAmount;
        }

        public void setUserAmount(Long userAmount) {
            this.userAmount = userAmount;
        }

        public Long getNonactiveUserAmount() {
            return nonactiveUserAmount;
        }

        public void setNonactiveUserAmount(Long nonactiveUserAmount) {
            this.nonactiveUserAmount = nonactiveUserAmount;
        }

        public Long getExpiredUserAmount() {
            return expiredUserAmount;
        }

        public void setExpiredUserAmount(Long expiredUserAmount) {
            this.expiredUserAmount = expiredUserAmount;
        }

        public Long getExpiringUserAmount() {
            return expiringUserAmount;
        }

        public void setExpiringUserAmount(Long expiringUserAmount) {
            this.expiringUserAmount = expiringUserAmount;
        }

        public Long getCreditRemaint() {
            return creditRemaint;
        }

        public void setCreditRemaint(Long creditRemaint) {
            this.creditRemaint = creditRemaint;
        }

        public Long getCreditAccumulation() {
            return creditAccumulation;
        }

        public void setCreditAccumulation(Long creditAccumulation) {
            this.creditAccumulation = creditAccumulation;
        }
    }

    @GetMapping("/departments")
    public Page<Department> departments(@RequestParam Optional<String> groupId,
                                        @RequestParam Optional<String> departmentName, @RequestParam Optional<String> adminName,
                                        @RequestParam Optional<String> adminUsername, @PageableDefault Pageable pageable) {
        Group group = null;
        Optional<Department> parent = Optional.empty();

        if (RchatUtils.isRchatAdmin()) {
            group = groupService.findOne(groupId.get()).orElseThrow(GroupNotFoundException::new);
        } else if (RchatUtils.isGroupAdmin()) {
            group = env.currentGroup();
        } else if (RchatUtils.isDepartmentAdmin()) {
            group = env.currentDepartment().getGroup();
            parent = Optional.of(env.currentDepartment());
        }
        if (parent.isPresent()) {
            return departmentViewService.search(group, parent.get(), departmentName, adminName, adminUsername,
                    pageable);
        } else {
            return departmentService.search(group, departmentName, adminName, adminUsername, pageable);
        }

    }

    @GetMapping("/talkback-groups")
    public Page<TalkbackGroup> talkbackGroups(Optional<String> groupId,
                                              Optional<String> name, @PageableDefault Pageable pageable) {

        Optional<Group> group = groupId.map(Group::new);

        if (RchatUtils.isGroupAdmin()) {
            group = Optional.of(env.currentGroup());
        }

        Optional<Department> department = Optional.empty();
        if (RchatUtils.isDepartmentAdmin()) {
            Department d = env.currentDepartment();

            department = Optional.of(d);
            group = Optional.of(d.getGroup());
        }

        return talkbackGroupService.search(group, department, name, pageable);
    }


    @GetMapping("/talkback-users")
    public Page<TalkbackUser> talkbackUsers(Optional<String> fullValue, Optional<TalkbackRole> role,
    		 								Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated,
                                            Optional<String> departmentId, Optional<String> groupId,
                                            Optional<String> agentId, Optional<Date> createdStart,
                                            Optional<Date> createdEnd, Optional<Date> renewStart,
                                            Optional<Date> renewEnd, @PageableDefault Pageable pageable) {
        Optional<Group> group = Optional.empty();
        Optional<Agent> agent = Optional.empty();
        Optional<Department> department = Optional.empty();

        if (RchatUtils.isGroupAdmin()) {
            group = Optional.of(env.currentGroup());
            agent = Optional.of(group.get().getAgent());

            if (departmentId.isPresent()) {
                department = departmentService.findByGroupAndId(env.currentGroup(), departmentId.get());
            }

        } else if (RchatUtils.isDepartmentAdmin()) {
            Department d = env.currentDepartment();
            department = Optional.of(d);
            group = Optional.of(d.getGroup());
            agent = Optional.of(d.getGroup().getAgent());
        } else {
            if (departmentId.isPresent()) {
                department = departmentService.findOne(departmentId.get());
            }
            if (groupId.isPresent()) {
                group = groupService.findOne(groupId.get());
            }
            if (agentId.isPresent()) {
                agent = agentService.findOne(agentId.get());
            }
        }
//        talkbackUserService.page(fullValue, role, shortValue, name, activated, departmentId, groupId, agentId, createdStart, createdEnd,
//                renewStart, renewEnd, pageable);
        
        return talkbackUserService.search(fullValue, role, shortValue, name, activated, department, group, agent, createdStart, createdEnd,
                renewStart, renewEnd, pageable);
    }

}
