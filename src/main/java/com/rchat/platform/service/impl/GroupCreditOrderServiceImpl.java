package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.GroupCreditAccountService;
import com.rchat.platform.service.GroupCreditOrderService;
import com.rchat.platform.web.exception.CreditInsufficiencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@SecurityService(ResourceType.CREDIT_ORDER)
public class GroupCreditOrderServiceImpl extends AbstractService<GroupCreditOrder, String> implements GroupCreditOrderService {
    @Autowired
    private RchatEnv env;
    @Autowired
    private GroupCreditOrderRepository repository;
    @Autowired
    private GroupCreditAccountService groupCreditAccountService;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private CreditAccountRenewLogRepository creditAccountRenewLogRepository;

    @Override
    protected JpaRepository<GroupCreditOrder, String> repository() {
        return repository;
    }

    @Override
    public Page<GroupCreditOrder> findByGroup(Group group, Pageable pageable) {
        return repository.findByGroup(group, pageable);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public CreditOrder distributeCredit(GroupCreditOrder order, long credit) {
        // 先检查当前用户额度账号余额是否充足
        CreditAccount respondentAccout = agentCreditAccountService.findByAgent(env.currentAgent());
        if (respondentAccout.getCredit() < credit) {
            throw new CreditInsufficiencyException();
        }
        respondentAccout.reduceCredit(credit);
        agentCreditAccountService.update(respondentAccout);

        // 下发额度
        order.distribute(credit);

        // 申请人额度增加
        CreditAccount account = groupCreditAccountService.findByGroup(order.getGroup());
        account.addCredit(credit);
        groupCreditAccountService.update(account);

        // 记录下发额度日志
        CreditAccountRenewLog log = new CreditAccountRenewLog();
        log.setToCreditAccount(account);
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
    public CreditOrder directDistribute(Group group, Long credit) {
        Agent agent = env.currentAgent();

        GroupCreditOrder order = new GroupCreditOrder();
        order.setCredit(credit);
        order.setGroup(group);
        order.setRespondent(agent);
        order.setStatus(CreditOrderStatus.UNREVIEWED);
        order.setDeadline(new Date());

        order = create(order);

        return distributeCredit(order, credit);
    }

    @Override
    public void deleteByGroup(Group group) {
        repository.deleteByGroup(group);
    }

}
