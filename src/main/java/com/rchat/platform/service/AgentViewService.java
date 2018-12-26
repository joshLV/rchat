package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentView;
import com.rchat.platform.domain.AgentViewId;

public interface AgentViewService extends GenericService<AgentView, AgentViewId> {
	List<Agent> findChildren(Agent parent);

	Page<Agent> findChildren(Agent parent, Pageable pageable);

	Page<Agent> search(Agent parent, Optional<String> agentName, Optional<String> adminName,
			Optional<String> adminUsername, Pageable pageable);

	Optional<Agent> findOneUnderAgent(String id, Agent parent);

}
