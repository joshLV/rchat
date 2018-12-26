package com.rchat.platform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.BusinessRent;
import com.rchat.platform.domain.BusinessRentRepository;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.service.BusinessRentService;

@Service
public class BusinessRentServiceImpl extends AbstractService<BusinessRent, String> implements BusinessRentService {
	@Autowired
	private BusinessRentRepository repository;

	@Override
	protected JpaRepository<BusinessRent, String> repository() {
		return repository;
	}

	@Override
	public List<BusinessRent> findByTalkbackUser(TalkbackUser user) {
		return repository.findByUser(user);
	}

}
