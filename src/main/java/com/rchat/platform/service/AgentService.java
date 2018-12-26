package com.rchat.platform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.User;

/**
 * 代理商业务Bean
 * 
 * @author dzhang
 *
 */
public interface AgentService extends GenericService<Agent, String> {
	List<Agent> findAgentTree(Agent parent, int depth);

	boolean hasAgents(Agent agent);

	/**
	 * 根据管理员查找代理商
	 * 
	 * @param user
	 *            管理员
	 * @return
	 */
	Optional<Agent> findByAdministrator(User user);

	/**
	 * 子代理商分页
	 * 
	 * @param agent
	 * @param pageable
	 * @return
	 */
	Page<Agent> findByParent(Agent agent, Pageable pageable);

	/**
	 * 查询出指定代理商的子代理商
	 * 
	 * @param agent
	 *            父代理商
	 * @return 子代理商列表
	 */
	List<Agent> findByParent(Agent parent);

	Optional<Agent> findParent(Agent child);

	Page<Agent> search(Optional<String> agentName, Optional<String> adminName, Optional<String> adminUsername,
			Pageable pageable);

	Optional<Agent> findByGroup(Group group);

	Page<Brief> findBriefs(Pageable pageable);

    List<Agent> findByLevel(Integer level);
}
