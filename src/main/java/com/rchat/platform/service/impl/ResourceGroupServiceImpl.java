package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.ResourceGroup;
import com.rchat.platform.domain.ResourceGroupRepository;
import com.rchat.platform.service.ResourceGroupService;

@Service
@SecurityService(ResourceType.RESOURCE_GROUP)
public class ResourceGroupServiceImpl extends AbstractService<ResourceGroup, String> implements ResourceGroupService {

	@Autowired
	private ResourceGroupRepository repository;

	@Override
	protected JpaRepository<ResourceGroup, String> repository() {
		return repository;
	}

}
