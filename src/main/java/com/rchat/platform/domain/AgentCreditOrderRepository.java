package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 代理商额度订单数据库接口
 * 
 * @author dzhang
 *
 */
@Repository
public interface AgentCreditOrderRepository extends JpaRepository<AgentCreditOrder, String> {
	/**
	 * 代理商自己申请的订单
	 * 
	 * @param agent
	 *            申请人
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findByAgent(Agent agent, Pageable pageable);

	/**
	 * 查询代理商申请和受理的所有订单
	 * 
	 * @param agent
	 *            申请代理商或者受理代理商
	 * @param respondent
	 *            受理代理商
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findByAgentOrRespondent(Agent agent, Agent respondent, Pageable pageable);

	/**
	 * 所有为完成的受理订单
	 * 
	 * @param agent
	 *            受理代理商
	 * @param completed
	 *            已完成
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findByRespondentAndStatusNot(Agent agent, CreditOrderStatus completed, Pageable pageable);

	/**
	 * 所有受理的订单
	 * 
	 * @param agent
	 *            受理代理商
	 * @param pageable
	 * @return
	 */
	Page<AgentCreditOrder> findByRespondent(Agent agent, Pageable pageable);

	void deleteByAgent(Agent agent);
}
