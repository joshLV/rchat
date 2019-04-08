package com.rchat.platform.service.impl;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.service.TalkbackNumberService;
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
public class GroupSegmentServiceImpl extends AbstractService<GroupSegment, String> implements GroupSegmentService {
    @Autowired
    private GroupSegmentRepository repository;
    @Autowired
    private TalkbackNumberService talkbackNumberService;

    @Override
    protected JpaRepository<GroupSegment, String> repository() {
        return repository;
    }

    private void fillHasUsers(List<GroupSegment> segments) {
        if (segments == null) {
            return;
        }
        segments.forEach(s -> s.setHasUsers(talkbackNumberService.existsByGroupSegment(s)));
    }

    @Override
    public List<GroupSegment> findAll() {
        List<GroupSegment> segments = super.findAll();

        fillHasUsers(segments);

        return segments;
    }

    @Override
    public Page<GroupSegment> findAll(Pageable pageable) {
        Page<GroupSegment> page = super.findAll(pageable);

        List<GroupSegment> segments = page.getContent();

        fillHasUsers(segments);

        return page;
    }
    @SecurityMethod(false)
    @Override
    public List<GroupSegment> findByGroup(Group group) {
        List<GroupSegment> segments = repository.findByGroup(group);

        fillHasUsers(segments);

        return segments;
    }

    @Override
    public Page<GroupSegment> findByGroup(Group group, Pageable pageable) {
        Page<GroupSegment> page = repository.findByGroup(group, pageable);

        List<GroupSegment> segments = page.getContent();

        fillHasUsers(segments);

        return page;
    }

    @Override
    public List<GroupSegment> findByAgentSegment(AgentSegment agentSegment) {
        List<GroupSegment> segments = repository.findByAgentSegment(agentSegment);

        fillHasUsers(segments);

        return segments;
    }
    @SecurityMethod(false)
    @Override
    public Optional<GroupSegment> findOne(String id) {
        Optional<GroupSegment> o = super.findOne(id);

        o.ifPresent(s -> fillHasUsers(Collections.singletonList(s)));

        return o;
    }

    @Override
    public boolean existsByAgentSegmentAndValue(AgentSegment agentSegment, int value) {
        return repository.existsByAgentSegmentAndValue(agentSegment, value);
    }

    @Override
    public Page<GroupSegment> findByInternal(boolean internal, Pageable pageable) {
        Page<GroupSegment> page = repository.findByInternal(internal, pageable);
        List<GroupSegment> segments = page.getContent();

        fillHasUsers(segments);

        return page;
    }

    @Override
    public Page<GroupSegment> findByAgentSegment(AgentSegment agentSegment, Pageable pageable) {
        Page<GroupSegment> page = repository.findByAgentSegment(agentSegment, pageable);

        List<GroupSegment> segments = page.getContent();
        fillHasUsers(segments);

        return page;
    }

    @Override
    public Page<GroupSegment> findByAgentSegment(AgentSegment agentSegment, boolean internal, Pageable pageable) {
        Page<GroupSegment> page = repository.findByAgentSegmentAndInternal(agentSegment, internal, pageable);

        List<GroupSegment> segments = page.getContent();
        fillHasUsers(segments);

        return page;
    }

    @Override
    public boolean existsByValue(int value) {
        return repository.existsByValue(value);
    }

    @Override
    public boolean existsByAgentSegment(AgentSegment segment) {
        return repository.existsByAgentSegment(segment);
    }

    @Override
    public boolean existsNonInternalSegments(Group group) {
        return repository.existsByGroupAndInternal(group, false);
    }

    @Override
    public Page<GroupSegment> findGroupSegments(Agent agent, Pageable pageable) {
        Page<GroupSegment> page = repository.findByAgentSegmentAgent(agent, pageable);

        List<GroupSegment> segments = page.getContent();
        fillHasUsers(segments);

        return page;
    }

    @Override
    public GroupSegment getInternalSegment(Group group) {
        return repository.findByGroupAndInternal(group, true);
    }

    @Override
    public void deleteByGroup(Group group) {
        repository.deleteByGroup(group);
    }

    @Override
    public Page<GroupSegment> findGroupSegments(Agent agent, boolean internal, Pageable pageable) {
        Page<GroupSegment> page = repository.findByAgentSegmentAgentAndInternal(agent, internal, pageable);

        List<GroupSegment> segments = page.getContent();
        fillHasUsers(segments);

        return page;
    }
}
