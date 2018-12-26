package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.ServiceException;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.CreditAccountService;
import com.rchat.platform.service.CreditTurnOverOrderService;
import com.rchat.platform.service.GroupCreditAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@SecurityService(ResourceType.CREDIT_ACCOUNT)
public class CreditTurnOverOrderServiceImpl extends AbstractService<CreditTurnOverOrder, String> implements CreditTurnOverOrderService {
    @Autowired
    private CreditTurnOverOrderRepository repository;
    @Autowired
    private GroupCreditTurnOverOrderRepository groupCreditTurnOverOrderRepository;
    @Autowired
    private AgentCreditTurnOverOrderRepository agentCreditTurnOverOrderRepository;

    @Autowired
    private CreditAccountService creditAccountService;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private GroupCreditAccountService groupCreditAccountService;
    @Autowired
    private RchatEnv env;

    @Override
    protected JpaRepository<CreditTurnOverOrder, String> repository() {
        return repository;
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public CreditTurnOverOrder ackOrder(CreditTurnOverOrder order) {
        if (order.getStatus() == TurnOverStatus.ACK) {
            throw new ServiceException("额度已经上缴");
        }

        Agent respondent = order.getRespondent();
        Agent agent = env.currentAgent();
        if (!agent.equals(respondent)) {
            throw new ServiceException("您不是此额度上缴订单的受理台管理员");
        }

        repository.ackOrder(order);
        order.setStatus(TurnOverStatus.ACK);

        CreditAccount parentAccount = agentCreditAccountService.findByAgent(respondent);
        CreditAccount account;
        if (order instanceof AgentCreditTurnOverOrder) {
            account = agentCreditAccountService.findByAgent(((AgentCreditTurnOverOrder) order).getAgent());
        } else if (order instanceof GroupCreditTurnOverOrder) {
            account = groupCreditAccountService.findByGroup(((GroupCreditTurnOverOrder) order).getGroup());
        } else {
            throw new ServiceException("未知上缴额度订单类型");
        }

        if (account.getCredit() < order.getCredit()) {
            throw new ServiceException("没有足够的上缴额度数量");
        }

        creditAccountService.turnOver(parentAccount, account, order.getCredit());
        return order;
    }

    @Override
    public Page<CreditTurnOverOrder> findByRespondent(Agent agent, Pageable pageable) {
        return repository.findByRespondent(agent, pageable);
    }

    @Override
    public Page<CreditTurnOverOrder> findAllByStatus(TurnOverStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    @Override
    public Page<CreditTurnOverOrder> findByRespondentAndStatus(Agent agent, TurnOverStatus status, Pageable pageable) {
        return repository.findByRespondentAndStatus(agent, status, pageable);
    }

    @Override
    public Page<CreditTurnOverOrder> findByGroup(Group group, Pageable pageable) {
        return groupCreditTurnOverOrderRepository.findByGroup(group, pageable);
    }

    @Override
    public Page<CreditTurnOverOrder> findByGroupAndStatus(Group group, TurnOverStatus status, Pageable pageable) {
        return groupCreditTurnOverOrderRepository.findByGroupAndStatus(group, status, pageable);
    }

    @Override
    public Page<CreditTurnOverOrder> findByAgentAndStatus(Agent agent, TurnOverStatus status, Pageable pageable) {
        return agentCreditTurnOverOrderRepository.findByAgentOrRespondentAndStatus(agent, agent, status, pageable);
    }

    @Override
    public Page<CreditTurnOverOrder> findByAgent(Agent agent, Pageable pageable) {
        return agentCreditTurnOverOrderRepository.findByAgentOrRespondent(agent, agent, pageable);
    }
}
