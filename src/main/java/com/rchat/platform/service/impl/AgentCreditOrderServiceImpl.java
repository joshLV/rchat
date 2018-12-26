package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.AgentCreditOrderService;
import com.rchat.platform.web.exception.CreditInsufficiencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@SecurityService(ResourceType.CREDIT_ORDER)
public class AgentCreditOrderServiceImpl extends AbstractService<AgentCreditOrder, String>
        implements AgentCreditOrderService {
    @Autowired
    private RchatEnv env;
    @Autowired
    private AgentCreditOrderRepository repository;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private CreditAccountRenewLogRepository creditAccountRenewLogRepository;


    @Override
    protected JpaRepository<AgentCreditOrder, String> repository() {
        return repository;
    }

    @Override
    public Page<AgentCreditOrder> findByAgent(Agent agent, Pageable pageable) {
        return repository.findByAgent(agent, pageable);
    }

    @Override
    public Page<AgentCreditOrder> findAll(Agent agent, Pageable pageable) {
        return repository.findByAgentOrRespondent(agent, agent, pageable);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public CreditOrder distributeCredit(AgentCreditOrder order, long credit) {
        // 先检查当前用户的额度账号额度是否充足
        CreditAccount respondentAccout = agentCreditAccountService.findByAgent(env.currentAgent());
        // 如果余额不足，抛出异常
        if (respondentAccout.getCredit() < credit) {
            throw new CreditInsufficiencyException();
        }

        // 余额充足，减掉余额
        respondentAccout.reduceCredit(credit);
        agentCreditAccountService.update(respondentAccout);

        // 下发额度
        order.distribute(credit);

        // 申请人额度账户增加额度
        CreditAccount applicantAccount = agentCreditAccountService.findByAgent(order.getAgent());
        applicantAccount.addCredit(credit);
        agentCreditAccountService.update(applicantAccount);

        // 下发额度日志
        CreditAccountRenewLog log = new CreditAccountRenewLog();
        log.setToCreditAccount(applicantAccount);
        log.setCreditAccount(respondentAccout);
        log.setCredit(credit);
        Date now = new Date();
        log.setCreatedAt(now);
        log.setUpdatedAt(now);

        creditAccountRenewLogRepository.save(log);

        return repository.saveAndFlush(order);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public CreditOrder directDistribute(Agent agent, Long credit) {
        Agent respondent = env.currentAgent();

        AgentCreditOrder order = new AgentCreditOrder();
        order.setCredit(credit);
        order.setAgent(agent);
        order.setRespondent(respondent);
        order.setStatus(CreditOrderStatus.UNREVIEWED);
        order.setDeadline(new Date());

        order = create(order);

        return distributeCredit(order, credit);
    }

    @Override
    public Page<AgentCreditOrder> findPendingOrders(Agent agent, Pageable pageable) {
        return repository.findByRespondentAndStatusNot(agent, CreditOrderStatus.COMPLETED, pageable);
    }

    @Override
    public Page<AgentCreditOrder> findReceivedOrders(Agent agent, Pageable pageable) {
        return repository.findByRespondent(agent, pageable);
    }

    @Override
    public Page<AgentCreditOrder> findSentOrders(Agent agent, Pageable pageable) {
        return repository.findByAgent(agent, pageable);
    }

    @Override
    public void deleteByAgent(Agent agent) {
        repository.deleteByAgent(agent);
    }
}
