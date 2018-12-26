package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.TaskConfiguration;
import com.rchat.platform.domain.TaskConfigurationRepository;
import com.rchat.platform.service.TaskConfigurationService;

@Service
public class TaskConfigurationServiceImpl extends AbstractService<TaskConfiguration, String>
		implements TaskConfigurationService {

	@Autowired
	private TaskConfigurationRepository repository;

	@Override
	protected JpaRepository<TaskConfiguration, String> repository() {
		return repository;
	}

}
