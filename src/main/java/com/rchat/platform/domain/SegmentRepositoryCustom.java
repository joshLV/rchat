package com.rchat.platform.domain;

public interface SegmentRepositoryCustom {
	boolean updateAgentSegment(AgentSegment segment);

	boolean updateGroupSegment(GroupSegment segment);

	boolean recycle(Segment segment);

	boolean existsAgentSegment(int value);

	boolean existsGroupSegment(int value);
}
