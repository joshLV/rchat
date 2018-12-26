package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentParent;

public interface AgentParentService extends GenericService<AgentParent, String> {
	/**
	 * 查找指定代理商所有的上级代理商
	 * 
	 * @param child
	 *            子代理商
	 * @return 上级代理商列表
	 */
	List<AgentParent> findParents(Agent child);

	/**
	 * 查找指定父代理商的所有下级代理商
	 * 
	 * @param parent
	 *            父代理商
	 * @return 子代理商列表
	 */
	List<AgentParent> findChildren(Agent parent);

	/**
	 * 查找指定代理商的指定级别的父级代理商
	 * 
	 * @param child
	 *            子代理商
	 * @param level
	 *            父代理商级别
	 * @return 指定级别的父级代理商
	 */
	Optional<AgentParent> findSpecifiedLevelParent(Agent child, int level);

	/**
	 * 查找指定代理商的指定级别的子代理商
	 * 
	 * @param parent
	 *            父代理商
	 * @param level
	 *            子代理商级别
	 * @return 同级别子代理商列表
	 */
	List<AgentParent> findSpecifiedLevelChild(Agent parent, int level);
}
