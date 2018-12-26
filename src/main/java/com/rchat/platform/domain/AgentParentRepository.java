package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentParentRepository extends JpaRepository<AgentParent, String> {
	List<AgentParent> findByAgentId(String agentId);

	List<AgentParent> findByParentId(String parentId);

	Optional<AgentParent> findByAgentIdAndLevel(String id, int level);

	List<AgentParent> findByParentIdAndLevel(String id, int level);
}
