package com.rchat.platform.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgentViewRepositoryCustom {

	Page<AgentView> search(Agent parent, Optional<String> agentName, Optional<String> adminName,
			Optional<String> adminUsername, Pageable pageable);
}
