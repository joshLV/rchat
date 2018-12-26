package com.rchat.platform.service;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.CreditTurnOverOrder;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.TurnOverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public interface CreditTurnOverOrderService extends GenericService<CreditTurnOverOrder, String> {
    /**
     * 确认订单，上缴额度
     *
     * @param order 上缴额度订单
     * @return 确认后的上缴额度订单
     */
    @Transactional
    CreditTurnOverOrder ackOrder(CreditTurnOverOrder order);

    Page<CreditTurnOverOrder> findByRespondent(Agent agent, Pageable pageable);

    Page<CreditTurnOverOrder> findAllByStatus(TurnOverStatus status, Pageable pageable);

    Page<CreditTurnOverOrder> findByRespondentAndStatus(Agent agent, TurnOverStatus status, Pageable pageable);

    Page<CreditTurnOverOrder> findByGroup(Group group, Pageable pageable);

    Page<CreditTurnOverOrder> findByGroupAndStatus(Group group, TurnOverStatus status, Pageable pageable);

    Page<CreditTurnOverOrder> findByAgentAndStatus(Agent agent, TurnOverStatus status, Pageable pageable);

    Page<CreditTurnOverOrder> findByAgent(Agent agent, Pageable pageable);
}
