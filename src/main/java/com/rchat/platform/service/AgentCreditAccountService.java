package com.rchat.platform.service;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.CreditAccount;

public interface AgentCreditAccountService extends CreditAccountService {
	CreditAccount findByAgent(Agent agent);

	void deleteByAgent(Agent agent);
}
