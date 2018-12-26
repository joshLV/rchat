package com.rchat.platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.domain.CreditOrder;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupCreditOrder;

public interface GroupCreditOrderService extends GenericService<GroupCreditOrder, String> {
	Page<GroupCreditOrder> findByGroup(Group group, Pageable pageable);

	/**
	 * 下集团额度订单
	 * 
	 * @param order
	 *            额度订单
	 * @param credit
	 *            下发数量
	 * @return 下发之后的订单
	 */
	CreditOrder distributeCredit(GroupCreditOrder order, long credit);

	void deleteByGroup(Group group);

    CreditOrder directDistribute(Group group, Long credit);
}
