package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentViewRepository extends JpaRepository<AgentView, AgentViewId>, AgentViewRepositoryCustom {
	Page<AgentView> findByViewIdParentId(String parentId, Pageable pageable);

	Optional<AgentView> findByViewId(AgentViewId agentViewId);

	List<AgentView> findByViewIdParentId(String id);
}
