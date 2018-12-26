package com.rchat.platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentCreditOrder;
import com.rchat.platform.domain.CreditOrder;

public interface AgentCreditOrderService extends GenericService<AgentCreditOrder, String> {

	Page<AgentCreditOrder> findByAgent(Agent agent, Pageable pageable);

	CreditOrder distributeCredit(AgentCreditOrder order, long credit);

	/**
	 * 查找指定代理商相关的所有订单，包括收到的和申请的
	 * 
	 * @param agent
	 *            代理商
	 * @param pageable
	 *            分页信息
	 * @return
	 */
	Page<AgentCreditOrder> findAll(Agent agent, Pageable pageable);

	/**
	 * 查找待处理的订单
	 * 
	 * @param agent
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findPendingOrders(Agent agent, Pageable pageable);

	/**
	 * 查找收到的订单
	 * 
	 * @param agent
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findReceivedOrders(Agent agent, Pageable pageable);

	/**
	 * 查找发出去的订单
	 * 
	 * @param agent
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findSentOrders(Agent agent, Pageable pageable);

	void deleteByAgent(Agent agent);

    CreditOrder directDistribute(Agent agent, Long credit);
}
