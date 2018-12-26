package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentCreditAccountRepository;
import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.service.AgentCreditAccountService;

@Service
@Transactional(readOnly = true)
@SecurityService(ResourceType.CREDIT_ACCOUNT)
public class AgentCreditAccountServiceImpl extends CreditAccountServiceImpl implements AgentCreditAccountService {
	@Autowired
	private AgentCreditAccountRepository repository;

	@Override
	public CreditAccount findByAgent(Agent agent) {
		return repository.findByAgent(agent);
	}

	@Override
	public void deleteByAgent(Agent agent) {
		repository.deleteByAgent(agent);
	}

}
