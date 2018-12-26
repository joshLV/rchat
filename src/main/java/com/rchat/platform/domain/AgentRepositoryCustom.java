package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AgentRepositoryCustom extends AgentSummaryRepository {
    Optional<Agent> findParent(Agent child);

    Page<Agent> search(Optional<String> agentName, Optional<String> adminName, Optional<String> adminUsername,
                       Pageable pageable);

    Optional<Agent> findByGroup(Group group);

    Page<Brief> findBriefs(Pageable pageable);
}
