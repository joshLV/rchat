package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.UnassignedSegment;
import com.rchat.platform.domain.UnassignedSegmentRepository;
import com.rchat.platform.service.UnassignedSegmentService;

@Service
@SecurityService(ResourceType.Segment)
public class UnassignedSegmentServiceImpl extends AbstractService<UnassignedSegment, String>
		implements UnassignedSegmentService {
	@Autowired
	private UnassignedSegmentRepository repository;

	@Override
	protected JpaRepository<UnassignedSegment, String> repository() {
		return repository;
	}
}
