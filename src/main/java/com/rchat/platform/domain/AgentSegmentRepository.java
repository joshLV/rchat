package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentSegmentRepository extends JpaRepository<AgentSegment, String> {
    List<AgentSegment> findByAgent(Agent owner);

    Page<AgentSegment> findByAgent(Agent agent, Pageable pageable);

    Page<AgentSegment> findByInternal(boolean internal, Pageable pageable);

    boolean existsByValue(int value);

    boolean existsByAgentAndInternal(Agent agent, boolean internal);

    AgentSegment findByAgentAndInternal(Agent agent, boolean internal);

    void deleteByAgent(Agent agent);
}
