package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.Resource;
import com.rchat.platform.domain.ResourceRepository;

@Service
public class ResourceService extends AbstractService<Resource, String>
		implements com.rchat.platform.service.ResourceService {
	@Autowired
	private ResourceRepository repository;

	@Override
	protected JpaRepository<Resource, String> repository() {
		return repository;
	}

}
