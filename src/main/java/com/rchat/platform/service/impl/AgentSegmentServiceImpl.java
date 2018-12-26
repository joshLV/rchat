package com.rchat.platform.service.impl;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import com.rchat.platform.domain.AgentSegmentRepository;
import com.rchat.platform.service.AgentSegmentService;
import com.rchat.platform.service.GroupSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@SecurityService(ResourceType.Segment)
public class AgentSegmentServiceImpl extends AbstractService<AgentSegment, String> implements AgentSegmentService {

    @Autowired
    private AgentSegmentRepository repository;
    @Autowired
    private GroupSegmentService groupSegmentService;

    @Override
    protected JpaRepository<AgentSegment, String> repository() {
        return repository;
    }

    private void fillHasUsers(List<AgentSegment> segments) {
        if (segments == null) {
            return;
        }
        segments.forEach(s -> s.setHasUsers(groupSegmentService.existsByAgentSegment(s)));
    }

    @Override
    public Optional<AgentSegment> findOne(String id) {
        Optional<AgentSegment> o = super.findOne(id);

        o.ifPresent(s -> fillHasUsers(Collections.singletonList(s)));

        return o;
    }

    @Override
    public List<AgentSegment> findByAgent(Agent agent) {
        List<AgentSegment> segments = repository.findByAgent(agent);

        fillHasUsers(segments);

        return segments;
    }

    @Override
    public Page<AgentSegment> findByAgent(Agent agent, Pageable pageable) {
        Page<AgentSegment> page = repository.findByAgent(agent, pageable);

        List<AgentSegment> segments = page.getContent();

        fillHasUsers(segments);

        return page;
    }

    @Override
    public Page<AgentSegment> findByInternal(boolean b, Pageable pageable) {
        Page<AgentSegment> page = repository.findByInternal(b, pageable);

        List<AgentSegment> segments = page.getContent();

        fillHasUsers(segments);

        return page;
    }

    @Override
    public List<AgentSegment> findAll() {
        List<AgentSegment> segments = super.findAll();

        fillHasUsers(segments);

        return segments;
    }

    @Override
    public Page<AgentSegment> findAll(Pageable pageable) {
        Page<AgentSegment> page = super.findAll(pageable);

        List<AgentSegment> segments = page.getContent();

        fillHasUsers(segments);

        return page;
    }

    @Override
    public boolean existsByValue(int value) {
        return repository.existsByValue(value);
    }

    @Override
    public boolean existsNonInternalSegments(Agent agent) {
        return repository.existsByAgentAndInternal(agent, false);
    }

    @Override
    public AgentSegment getInternalSegment(Agent agent) {
        return repository.findByAgentAndInternal(agent, true);
    }

    @Override
    public void deleteByAgent(Agent agent) {
        repository.deleteByAgent(agent);
    }
}
