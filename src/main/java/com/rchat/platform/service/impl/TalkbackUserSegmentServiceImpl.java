package com.rchat.platform.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rchat.platform.domain.TalkbackUserSegment;
import com.rchat.platform.service.TalkbackUserSegmentService;

//@Service
//@SecurityService(ResourceType.Segment)
public class TalkbackUserSegmentServiceImpl extends AbstractService<TalkbackUserSegment, String>
		implements TalkbackUserSegmentService {

	@Override
	protected JpaRepository<TalkbackUserSegment, String> repository() {
		return null;
	}
}
