package com.rchat.platform.service;

import com.rchat.platform.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 集团业务
 *
 * @author dzhang
 * @since 2017-04-04 20:25:20
 */
public interface GroupService extends GenericService<Group, String> {

    /**
     * 根据代理商查找代理商创建的集团
     *
     * @param agent    代理商
     * @param pageable 分页信息
     * @return 集团列表
     */
    Page<Group> findByAgent(Agent agent, Pageable pageable);

    boolean hasGroups(Agent agent);

    /**
     * 根据管理员查找集团
     *
     * @param user 管理员
     * @return 管理员管理的集团
     */
    Optional<Group> findByAdministrator(User user);

    /**
     * 根据代理商查找集团
     *
     * @param agent 代理商
     * @return 集团列表
     */
    List<Group> findByAgent(Agent agent);

    Optional<Group> findByIdAndAgent(String id, Agent currentAgent);

    Page<Group> search(Optional<String> agentName, Optional<String> groupName, Optional<String> adminName,
                       Optional<String> adminUsername, Optional<Boolean> status, Pageable pageable);

    Page<Brief> findBriefs(Pageable pageable);

    /**
     * 设置服务器
     *
     * @param groups 要设置的集团列表
     * @param server 服务器
     */
    void setServer(List<Group> groups, Server server);

    /**
     * 判断指定的集团是否分配了语音服务器
     *
     * @param group 集团
     * @return 如果分配了语音服务器，返回<code>true</code>，否则，返回<code>false</code>
     */
    boolean existsServer(Group group);

    Page<Brief> findBriefs(Server server, Pageable pageable);
}
