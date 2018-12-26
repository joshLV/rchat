package com.rchat.platform.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.domain.CreditAccountRepository;
import com.rchat.platform.service.CreditAccountService;

@Service
@Transactional(readOnly = true)
@Primary
@SecurityService(ResourceType.CREDIT_ACCOUNT)
public class CreditAccountServiceImpl extends AbstractService<CreditAccount, String> implements CreditAccountService {
	@Autowired
	private CreditAccountRepository repository;

	@Override
	protected JpaRepository<CreditAccount, String> repository() {
		return repository;
	}

	@Override
	@SecurityMethod(operation = OperationType.UPDATE)
	@Transactional
	public void turnOver(CreditAccount parentAccount, CreditAccount account, long credit) {
		parentAccount.addCredit(credit);
		account.reduceCredit(credit);

		repository.save(Arrays.asList(parentAccount, account));
	}
}
