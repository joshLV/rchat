package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupCreditAccountRepository;
import com.rchat.platform.service.GroupCreditAccountService;

@Service
@Transactional(readOnly = true)
@SecurityService(ResourceType.CREDIT_ACCOUNT)
public class GroupCreditAccountServiceImpl extends CreditAccountServiceImpl implements GroupCreditAccountService {
	@Autowired
	private GroupCreditAccountRepository repository;

	@Override
	public CreditAccount findByGroup(Group group) {
		return repository.findByGroup(group);
	}

	@Override
	public void deleteByGroup(Group group) {
		repository.deleteByGroup(group);
	}
}
