package com.rchat.platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.CreditOrder;

public interface CreditOrderService extends GenericService<CreditOrder, String> {

	Page<CreditOrder> findReceivedOrders(Agent agent, Pageable pageable);

	Page<CreditOrder> findPendingOrders(Agent agent, Pageable pageable);

	Page<CreditOrder> findAll(Agent agent, Pageable pageable);

    CreditOrder cancel(CreditOrder order);
}
