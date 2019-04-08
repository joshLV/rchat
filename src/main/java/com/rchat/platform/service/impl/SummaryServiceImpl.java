package com.rchat.platform.service.impl;

import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.common.DateUtils;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.GroupCreditAccountService;
import com.rchat.platform.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SummaryServiceImpl implements SummaryService {
    @Autowired
    private RchatEnv env;

    @Autowired
    private TalkbackUserRepository talkbackUserRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private RenewLogRepository renewLogRepository;
    @Autowired
    private CreditAccountRenewLogRepository creditAccountRenewLogRepository;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private GroupCreditAccountService groupCreditAccountService;

    @Override
    public Summary count() {
        if (RchatUtils.isRchatAdmin()) {
            return doCount(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return doCount(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return doCount(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return doCount(null, null, env.currentDepartment());
        }
        return new Summary();
    }

    @Override
    public CreditSummary countCredit(Agent agent) {
        CreditAccount account = agentCreditAccountService.findByAgent(agent);
        return doCount(account);
    }

    private CreditSummary doCount(CreditAccount account) {
        CreditSummary summary = new CreditSummary();

        summary.setCreditRemaint(account.getCredit());
        summary.setCreditAccumulation(creditAccountRenewLogRepository.countAccumulation(account, null, null));
        summary.setCreditTodayAccumulation(creditAccountRenewLogRepository.countAccumulation(account, DateUtils.currentDateStartTime(), null));
        summary.setCreditMonthAccumulation(creditAccountRenewLogRepository.countAccumulation(account, DateUtils.currentMonthStartTime(), null));
        summary.setCreditSeasonAccumulation(creditAccountRenewLogRepository.countAccumulation(account, DateUtils.currentSeasonStartTime(), null));
        summary.setCreditYearAccumulation(creditAccountRenewLogRepository.countAccumulation(account, DateUtils.currentYearStartTime(), null));

        summary.setCreditConsumption(renewLogRepository.countConsumption(account, null, null));
        summary.setCreditTodayConsumption(renewLogRepository.countTodayConsumption(account));
        summary.setCreditMonthConsumption(renewLogRepository.countConsumption(account, DateUtils.currentMonthStartTime(), null));
        summary.setCreditSeasonConsumption(renewLogRepository.countConsumption(account, DateUtils.currentSeasonStartTime(), null));
        summary.setCreditYearConsumption(renewLogRepository.countConsumption(account, DateUtils.currentYearStartTime(), null));
        return summary;
    }

    @Override
    public CreditSummary countCredit(Group group) {
        CreditAccount account = groupCreditAccountService.findByGroup(group);
        return doCount(account);
    }

    @Override
    public Summary count(Agent agent) {
        return doCount(agent, null, null);
    }

    @Override
    public Summary count(Group group) {
        return doCount(null, group, null);
    }
    @SecurityMethod(false)
    @Override
    public Summary count(Department department) {
        return doCount(null, null, department);
    }

    private Summary doCount(Agent agent, Group group, Department department) {
        Summary summary = new Summary();

        summary.setUserAmount(talkbackUserRepository.countTotalAmount(agent, group, department));
        summary.setExpiredUserAmount(talkbackUserRepository.countExpiredAmount(agent, group, department));
        summary.setNewExpiredUserAmmount(talkbackUserRepository.countNewExpiredAmount(agent, group, department));
        summary.setNewUserAmount(talkbackUserRepository.countNewAmount(agent, group, department));
        summary.setExpiredUserAmount(talkbackUserRepository.countExpiredAmount(agent, group, department));
        summary.setExpiringUserAmount(talkbackUserRepository.countExpiringAmount(agent, group, department));
        summary.setNonactiveUserAmount(talkbackUserRepository.countNonactivatedAmount(agent, group, department));
        summary.setNewActivatedUserAmmout(talkbackUserRepository.countNewActivatedAmount(agent, group, department));

        if (RchatUtils.isRchatAdmin() || RchatUtils.isAgentAdmin()) {
            summary.setFirstAgentAmount(agentRepository.countTotalAmount(agent, 1));
            summary.setSecondAgentAmount(agentRepository.countTotalAmount(agent, 2));
            summary.setThirdAgentAmount(agentRepository.countTotalAmount(agent, 3));
            summary.setForthAgentAmount(agentRepository.countTotalAmount(agent, 4));
            summary.setFifthAgentAmount(agentRepository.countTotalAmount(agent, 5));
        }

        summary.setNewAgentAmount(agentRepository.countNewAmount(agent));

        summary.setGroupAmount(groupRepository.countTotalAmount(agent));
        summary.setNewGroupAmount(groupRepository.countNewAmount(agent));


        CreditAccount account;
        if (agent != null) {
            account = agentCreditAccountService.findByAgent(agent);
            summary.setCreditRemaint(account.getCredit());
        } else if (group != null) {
            account = groupCreditAccountService.findByGroup(group);
            summary.setCreditRemaint(account.getCredit());
        } else {
            account = null;
        }
        summary.setCreditConsumption(renewLogRepository.countTodayConsumption(account));
        summary.setCreditAccumulation(creditAccountRenewLogRepository.countAccumulation(account, null, null));

        return summary;
    }
}
