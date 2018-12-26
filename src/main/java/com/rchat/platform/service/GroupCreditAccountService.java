package com.rchat.platform.service;

import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.domain.Group;

public interface GroupCreditAccountService extends CreditAccountService {
	CreditAccount findByGroup(Group group);

	void deleteByGroup(Group group);
}
