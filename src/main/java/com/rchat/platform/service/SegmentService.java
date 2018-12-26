package com.rchat.platform.service;

import java.util.List;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.Segment;

/**
 * 号段业务接口
 * 
 * @author dzhang
 */
public interface SegmentService extends GenericService<Segment, String> {

	/**
	 * 将号段分配给集团
	 * 
	 * @param segment
	 *            号段
	 * @param group
	 *            集团
	 * @return 分配之后的号段
	 */
	Segment grantGroup(Segment segment, Group group);

	/**
	 * 将号段分配给代理商
	 * 
	 * @param segment
	 *            号段
	 * @param agent
	 *            代理商
	 * @return 分配之后号段
	 */
	Segment grantAgent(Segment segment, Agent agent);

	/**
	 * 回收号段
	 * 
	 * @param segment
	 *            号段
	 * @return
	 */
	Segment recycle(Segment segment);

	List<? extends Segment> findAgentAvailableSegments();

	/**
	 * 是否存在代理商号段，包括代理商保留号段
	 * 
	 * @param value
	 * @return
	 */
	boolean existsAgentSegment(int value);

	/**
	 * 是否存在集团号段，包括保留号段
	 * 
	 * @param value
	 * @return
	 */
	boolean existsGroupSegment(int value);

}
