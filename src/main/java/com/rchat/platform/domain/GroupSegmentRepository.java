package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupSegmentRepository extends JpaRepository<GroupSegment, String> {
	List<GroupSegment> findByGroup(Group owner);

	List<GroupSegment> findByAgentSegment(AgentSegment agentSegment);

	boolean existsByAgentSegmentAndValue(AgentSegment agentSegment, int value);

	Page<GroupSegment> findByGroup(Group group, Pageable pageable);

	Page<GroupSegment> findByInternal(boolean internal, Pageable pageable);

	boolean existsByValue(int value);

	boolean existsByAgentSegment(AgentSegment segment);

	Page<GroupSegment> findByAgentSegment(AgentSegment agentSegment, Pageable pageable);

	Page<GroupSegment> findByAgentSegmentAndInternal(AgentSegment agentSegment, boolean internal, Pageable pageable);

	Page<GroupSegment> findByAgentSegmentAgent(Agent agent, Pageable pageable);

	Page<GroupSegment> findByAgentSegmentAgentAndInternal(Agent agent, boolean internal, Pageable pageable);

    boolean existsByGroupAndInternal(Group group, boolean internal);

	GroupSegment findByGroupAndInternal(Group group, boolean b);

    void deleteByGroup(Group group);
}
