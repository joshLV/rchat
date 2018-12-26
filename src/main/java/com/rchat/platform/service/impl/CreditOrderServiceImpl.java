package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.CreditOrder;
import com.rchat.platform.domain.CreditOrderRepository;
import com.rchat.platform.domain.CreditOrderStatus;
import com.rchat.platform.service.CreditOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@Transactional(readOnly = true)
@SecurityService(ResourceType.CREDIT_ORDER)
public class CreditOrderServiceImpl extends AbstractService<CreditOrder, String> implements CreditOrderService {
    @Autowired
    private CreditOrderRepository repository;

    @Override
    public Page<CreditOrder> findReceivedOrders(Agent agent, Pageable pageable) {
        return repository.findByRespondent(agent, pageable);
    }

    @Override
    protected JpaRepository<CreditOrder, String> repository() {
        return repository;
    }

    @Override
    public Page<CreditOrder> findPendingOrders(Agent agent, Pageable pageable) {
        return repository.findByRespondentAndStatusNot(agent, CreditOrderStatus.COMPLETED, pageable);
    }

    @Override
    public Page<CreditOrder> findAll(Agent agent, Pageable pageable) {
        return repository.findByRespondentOrAgent(agent, pageable);
    }

    @Override
    @SecurityMethod(operation = OperationType.UPDATE)
    @Transactional
    public CreditOrder cancel(CreditOrder order) {
        repository.cancelOrder(order);
        order.setStatus(CreditOrderStatus.CANCELED);
        return order;
    }
}
