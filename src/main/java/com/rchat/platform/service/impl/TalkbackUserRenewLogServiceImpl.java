package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.AgentService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.TalkbackUserRenewLogService;
import com.rchat.platform.web.exception.AgentNotFoundException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@SecurityService(ResourceType.RENEW_LOG)
public class TalkbackUserRenewLogServiceImpl extends AbstractService<TalkbackUserRenewLog, String> implements TalkbackUserRenewLogService {
    @Autowired
    private AgentService agentService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TalkbackUserRenewLogRepository repository;
    @Autowired
    private RchatEnv env;

    @Override
    public Page<TalkbackUserRenewLog> findByUser(TalkbackUser user, Pageable pageable) {
        return repository.findByUser(user, pageable);
    }

    @Override
    public Page<TalkbackUserRenewLog> findByUserGroup(Group group, Pageable pageable) {
        return repository.findByUserGroup(group, pageable);
    }

    @Override
    public Page<TalkbackUserRenewLog> findByCreatedAtBetween(Date start, Date end, Pageable pageable) {
        return repository.findByCreatedAtBetween(start, end, pageable);
    }

    @Override
    public Page<TalkbackUserRenewLog> findByUserGroupAgent(Agent agent, Pageable pageable) {
        return repository.findByUserGroupAgent(agent, pageable);
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUserRenewLog> statRenewLogs(String agentId, String groupId, Date start, Date end, String fullValue, Pageable pageable) {
        Agent agent;
        Group group;
        if (RchatUtils.isRchatAdmin()) {
            if (agentId == null) {
                agent = null;
            } else {
                agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
            }

            if (groupId == null) {
                group = null;
            } else {
                group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
            }
        } else if (RchatUtils.isAgentAdmin()) {
            agent = env.currentAgent();
            group = null;
        } else if (RchatUtils.isGroupAdmin()) {
            agent = null;
            group = env.currentGroup();
        } else {
            return new PageImpl<>(Collections.emptyList());
        }

        return repository.statRenewLogs(agent, group, start, end, fullValue, pageable);
    }

    @Override
    @SecurityMethod(false)
    public List<TalkbackUserRenewLog> findRenewLogs(String agentId, String groupId, Date start, Date end, String fullValue) {
        Agent agent;
        Group group;
        if (RchatUtils.isRchatAdmin()) {
            if (agentId == null) {
                agent = null;
            } else {
                agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
            }

            if (groupId == null) {
                group = null;
            } else {
                group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
            }
        } else if (RchatUtils.isAgentAdmin()) {
            agent = env.currentAgent();
            group = null;
        } else if (RchatUtils.isGroupAdmin()) {
            agent = null;
            group = env.currentGroup();
        } else {
            return Collections.emptyList();
        }
        return repository.findRenewLogs(agent, group, start, end, fullValue);
    }

    @Override
    protected JpaRepository<TalkbackUserRenewLog, String> repository() {
        return repository;
    }
}
