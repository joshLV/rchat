package com.rchat.platform.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentParent;
import com.rchat.platform.domain.AgentParentRepository;
import com.rchat.platform.service.AgentParentService;

@Service
public class AgentParentServiceImpl extends AbstractService<AgentParent, String> implements AgentParentService {
	@Autowired
	private AgentParentRepository repository;

	@Override
	public List<AgentParent> findParents(Agent agent) {
		if (agent == null) {
			return Collections.emptyList();
		}

		return repository.findByAgentId(agent.getId());
	}

	@Override
	public List<AgentParent> findChildren(Agent parent) {
		return repository.findByParentId(parent.getId());
	}

	@Override
	public Optional<AgentParent> findSpecifiedLevelParent(Agent agent, int level) {
		if (level > agent.getLevel()) {
			return Optional.empty();
		}

		return repository.findByAgentIdAndLevel(agent.getId(), level);
	}

	@Override
	public List<AgentParent> findSpecifiedLevelChild(Agent parent, int level) {
		if (level < parent.getLevel()) {
			return Collections.emptyList();
		}

		return repository.findByParentIdAndLevel(parent.getId(), level);
	}

	@Override
	protected JpaRepository<AgentParent, String> repository() {
		return repository;
	}

}
