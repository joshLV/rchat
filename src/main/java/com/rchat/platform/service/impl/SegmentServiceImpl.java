package com.rchat.platform.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.BeanSelfAware;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.Segment;
import com.rchat.platform.domain.SegmentRepository;
import com.rchat.platform.service.SegmentService;

@Service
@SecurityService(ResourceType.Segment)
public class SegmentServiceImpl extends AbstractService<Segment, String>
		implements SegmentService, BeanSelfAware<SegmentService> {
	@Autowired
	private SegmentRepository repository;
	private SegmentService self;

	@Override
	protected JpaRepository<Segment, String> repository() {
		return repository;
	}

	@Override
	@Transactional
	public Segment update(Segment segment) {
		if (segment instanceof AgentSegment) {
			AgentSegment s = (AgentSegment) segment;
			if (repository.updateAgentSegment(s))
				return repository.findOne(segment.getId());
		} else if (segment instanceof GroupSegment) {
			GroupSegment s = (GroupSegment) segment;
			if (repository.updateGroupSegment(s))
				return repository.findOne(segment.getId());
		}

		return super.update(segment);
	}

	@Override
	@Transactional
	@SecurityMethod(operation = OperationType.UPDATE)
	public Segment grantGroup(Segment segment, Group group) {
		GroupSegment groupSegment = new GroupSegment(segment);
		groupSegment.setGroup(group);

		return self.update(groupSegment);
	}

	@Override
	@SecurityMethod(false)
	public void setBeanSelf(SegmentService self) {
		this.self = self;
	}

	@Override
	@Transactional
	@SecurityMethod(operation = OperationType.UPDATE)
	public Segment grantAgent(Segment segment, Agent agent) {
		AgentSegment agentSegment = new AgentSegment(segment);
		agentSegment.setAgent(agent);

		return self.update(agentSegment);
	}

	@Override
	@Transactional
	@SecurityMethod(operation = OperationType.UPDATE)
	public Segment recycle(Segment segment) {
		repository.recycle(segment);

		return repository.findOne(segment.getId());
	}

	@Override
	public List<? extends Segment> findAgentAvailableSegments() {
		return repository.findAgentAvailableSegments();
	}

	@Override
	public boolean existsAgentSegment(int value) {
		return repository.existsAgentSegment(value);
	}

	@Override
	public boolean existsGroupSegment(int value) {
		return repository.existsGroupSegment(value);
	}
}
