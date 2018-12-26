package com.rchat.platform.service;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupSegmentService extends GenericService<GroupSegment, String> {
    List<GroupSegment> findByGroup(Group group);

    List<GroupSegment> findByAgentSegment(AgentSegment agentSegment);

    boolean existsByAgentSegmentAndValue(AgentSegment agentSegment, int value);

    Page<GroupSegment> findByGroup(Group group, Pageable pageable);

    Page<GroupSegment> findByInternal(boolean internal, Pageable pageable);

    boolean existsByValue(int value);

    boolean existsByAgentSegment(AgentSegment segment);

    /**
     * 判断指定集团是否存在非默认号段
     *
     * @param group 待判断的集团
     * @return 如果存在，返回<code>true</code>，否则，返回<code>false</code>
     */
    boolean existsNonInternalSegments(Group group);

    Page<GroupSegment> findByAgentSegment(AgentSegment agentSegment, Pageable pageable);

    Page<GroupSegment> findByAgentSegment(AgentSegment agentSegment, boolean booleanValue, Pageable pageable);

    Page<GroupSegment> findGroupSegments(Agent agent, Pageable pageable);

    Page<GroupSegment> findGroupSegments(Agent agent, boolean internal, Pageable pageable);

    GroupSegment getInternalSegment(Group group);

    void deleteByGroup(Group group);
}
