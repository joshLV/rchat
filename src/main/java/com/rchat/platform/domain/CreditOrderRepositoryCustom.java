package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CreditOrderRepositoryCustom {
	/**
	 * 分页查找指定代理商的全部额度订单，包括收到和发出的
	 * 
	 * @param agent
	 *            代理商
	 * @param pageable
	 *            分页信息
	 * @return
	 */
	Page<CreditOrder> findByRespondentOrAgent(Agent agent, Pageable pageable);
}
