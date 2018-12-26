package com.rchat.platform.service;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgentSegmentService extends GenericService<AgentSegment, String> {
    List<AgentSegment> findByAgent(Agent agent);

    Page<AgentSegment> findByAgent(Agent agent, Pageable pageable);

    Page<AgentSegment> findByInternal(boolean b, Pageable pageable);

    boolean existsByValue(int value);

    boolean existsNonInternalSegments(Agent agent);

    /**
     * 获取指定代理商默认号段
     *
     * @param agent 代理商
     * @return 代理商默认号段
     */
    AgentSegment getInternalSegment(Agent agent);

    void deleteByAgent(Agent agent);
}
