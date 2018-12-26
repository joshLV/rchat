package com.rchat.platform.service.impl;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.DateUtils;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.GroupCreditAccountService;
import com.rchat.platform.service.RenewLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@SecurityService(ResourceType.RENEW_LOG)
public class RenewLogServiceImpl extends AbstractService<RenewLog, String> implements RenewLogService {
    @Autowired
    private RenewLogRepository repository;
    @Autowired
    private CreditAccountRenewLogRepository creditAccountRenewLogRepository;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private GroupCreditAccountService groupCreditAccountService;
    @Autowired
    private RchatEnv env;


    @Override
    protected JpaRepository<RenewLog, String> repository() {
        return repository;
    }

    @Override
    public Page<RenewLog> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    public List<RenewLog> findYearConsumption(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return repository.findConsumption(account, DateUtils.currentYearStartTime(), null);
    }

    @Override
    public List<RenewLog> findSeasonConsumption(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return repository.findConsumption(account, DateUtils.currentSeasonStartTime(), null);
    }

    @Override
    public List<RenewLog> findMonthConsumption(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return repository.findConsumption(account, DateUtils.currentMonthStartTime(), null);
    }

    @Override
    public List<RenewLog> findConsumption(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return repository.findConsumption(account, null, null);
    }

    @Override
    public List<RenewLog> findYearConsumption(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return repository.findConsumption(account, DateUtils.currentYearStartTime(), null);

    }

    @Override
    public List<RenewLog> findSeasonConsumption(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return repository.findConsumption(account, DateUtils.currentSeasonStartTime(), null);
    }

    @Override
    public List<RenewLog> findMonthConsumption(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return repository.findConsumption(account, DateUtils.currentMonthStartTime(), null);
    }

    @Override
    public List<RenewLog> findConsumption(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return repository.findConsumption(account, null, null);
    }

    @Override
    public List<? extends RenewLog> findYearAccumulation(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return creditAccountRenewLogRepository.findAccumulation(account, DateUtils.currentYearStartTime(), null);
    }

    @Override
    public List<? extends RenewLog> findSeasonAccumulation(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return creditAccountRenewLogRepository.findAccumulation(account, DateUtils.currentSeasonStartTime(), null);
    }

    @Override
    public List<? extends RenewLog> findMonthAccumulation(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return creditAccountRenewLogRepository.findAccumulation(account, DateUtils.currentMonthStartTime(), null);

    }

    @Override
    public List<? extends RenewLog> findAccumulation(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return creditAccountRenewLogRepository.findAccumulation(account, null, null);
    }

    @Override
    public List<? extends RenewLog> findYearAccumulation(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return creditAccountRenewLogRepository.findAccumulation(account, DateUtils.currentYearStartTime(), null);
    }

    @Override
    public List<? extends RenewLog> findSeasonAccumulation(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return creditAccountRenewLogRepository.findAccumulation(account, DateUtils.currentSeasonStartTime(), null);
    }

    @Override
    public List<? extends RenewLog> findMonthAccumulation(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return creditAccountRenewLogRepository.findAccumulation(account, DateUtils.currentMonthStartTime(), null);
    }

    @Override
    public List<? extends RenewLog> findAccumulation(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return creditAccountRenewLogRepository.findAccumulation(account, null, null);
    }

    @Override
    public List<? extends RenewLog> findAccumulation() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findAccumulation(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findAccumulation(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findYearAccumulation() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findYearAccumulation(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findYearAccumulation(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findSeasonAccumulation() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findSeasonAccumulation(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findSeasonAccumulation(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findMonthAccumulation() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findMonthAccumulation(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findMonthAccumulation(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findConsumption() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findConsumption(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findConsumption(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findYearConsumption() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findYearConsumption(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findYearConsumption(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findSeasonConsumption() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findSeasonConsumption(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findSeasonConsumption(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<? extends RenewLog> findMonthConsumption() {
        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            return findMonthConsumption(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return findMonthConsumption(env.currentGroup());
        } else {
            return Collections.emptyList();
        }
    }
}
