package com.rchat.platform.service.impl;

import com.rchat.platform.aop.*;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.CannotDeleteExeption;
import com.rchat.platform.exception.SegmentConflictException;
import com.rchat.platform.exception.SegmentExhaustionException;
import com.rchat.platform.exception.UnkownAgentTypeException;
import com.rchat.platform.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Transactional(readOnly = true)
@SecurityService(ResourceType.AGENT)
public class AgentServiceImpl extends AbstractService<Agent, String> implements AgentService {
    private static final Log log = LogFactory.getLog(AgentServiceImpl.class);
    @Autowired
    private AgentRepository repository;
    @Autowired
    private AgentParentService agentParentService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentCreditAccountService creditAccountService;
    @Autowired
    private AgentCreditOrderService creditOrderService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AgentSegmentService agentSegmentService;
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private SegmentService segmentService;

    @Override
    @Transactional
    public Agent create(@SecurityResource Agent agent) {
        // 创建管理员
        AgentType type = agent.getType();
        Role role;

        switch (type) {
            // 不能创建 超级管理台用户
            // case RCHAT:
            // 平台管理员
            // role = new Role("32b62a97-502f-4164-ab7d-1f3c50bad1e0");
            // break;
            case NORMAL:
                // 普通代理商
                role = new Role("96cadace-4b09-4a7d-8fe1-a080f94ebbc6");
                break;
            case TERMINAL:
                // 终端代理商
                role = new Role("d3a9f6ab-fd16-4916-bb69-cbc4e202a8ae");
                break;
            default:
                throw new UnkownAgentTypeException();
        }

        User administrator = agent.getAdministrator();
        administrator.setRoles(Collections.singletonList(role));
        administrator = userService.create(administrator);
        agent.setAdministrator(administrator);

        // 创建代理商
        agent = super.create(agent);

        // 创建额度账户
        AgentCreditAccount account = new AgentCreditAccount();
        account.setAgent(agent);

        CreditAccount a = creditAccountService.create(account);
        log.debug(a);

        // 创建默认号段
        List<? extends Segment> segments = segmentService.findAgentAvailableSegments();

        AgentSegment segment = agent.getSegment();
        // level == 1 说明是超级受理台创建的一级代理商
        if (agent.getLevel() == 1) {
            int value = segment.getValue();
            // 查看冲突的号段，比对前两位
            Predicate<Segment> predicate = s -> s.getValue() / 100 == value;
            if (segments.parallelStream().anyMatch(predicate))
                throw new SegmentConflictException();

            segment.setValue(value * 100 + 1);
        } else {
            // 获取父级代理商号段的前两位用于后两位自增
            final int value = segment.getValue() / 100 * 100;
            int subfix = 1;
            for(; subfix < 100; subfix++) {
            	// 拼接号段，后两位自增，自增范围是 1 - 99
                int tmpValue = value + subfix;
            	 if (!segmentService.existsAgentSegment(tmpValue)) {
                     segment = new AgentSegment();
                     segment.setValue(tmpValue);
                     break;
            	 }
            }

            // 如果大于99，就不能自增了
            if (subfix > 99) {
                throw new SegmentExhaustionException();
            }

        }
        segment.setAgent(agent);
        segment.setInternal(true);
        segment = agentSegmentService.create(segment);
        agent.setSegment(segment);

        // 保存代理商所有的父类信息
        // 先取得直接父级代理商的全部父代理商
        String agentId = agent.getId();
        Optional.ofNullable(agent.getParent()).ifPresent(parent -> {
            List<AgentParent> parentParents = agentParentService.findParents(parent);
            // 创建自己的父级代理商，增加直接父级
            List<AgentParent> parents = new ArrayList<>();
            parentParents.forEach(p -> parents.add(new AgentParent(agentId, p.getParentId(), p.getLevel())));

            String parentId = parent.getId();
            parents.add(new AgentParent(agentId, parentId, parent.getLevel()));

            agentParentService.create(parents);
        });

        return agent;
    }

    @Override
    @Transactional
    public Agent update(Agent entity) {
        userService.update(entity.getAdministrator());

        return super.update(entity);
    }

    @Override
    public boolean delete(Agent agent) {
        assertCanDelete(agent);

        agentSegmentService.deleteByAgent(agent);
        creditAccountService.deleteByAgent(agent);
        creditOrderService.deleteByAgent(agent);

        return super.delete(agent);
    }

    /**
     * 断言指定代理商是否能被删除，如果代理删存在非默认号段，则不能删除，其他资源则由数据库外键约束
     *
     * @param agent 待检测的代理商
     */
    private void assertCanDelete(Agent agent) {
        // 判断代理商下面是否存在非默认号段
        if (agentSegmentService.existsNonInternalSegments(agent))
            throw new CannotDeleteExeption("代理商存在非默认号段，不能删除");

        // 判断默认号段下面是否存在集团号段
        AgentSegment segment = agentSegmentService.getInternalSegment(agent);
        if (groupSegmentService.existsByAgentSegment(segment))
            throw new CannotDeleteExeption("代理商默认号段下，还存在集团号段，不能删除");

    }

    @Override
    public List<Agent> findAgentTree(Agent parent, int depth) {
        if (depth <= 0) {
            return Collections.emptyList();
        }

        --depth;

        List<Agent> agents;
        if (parent == null) {
            agents = repository.findByParentIsNull();
        } else {
            agents = repository.findByParent(parent);
        }

        for (Agent agent : agents) {
            agent.setChildren(findAgentTree(agent, depth));
            if (RchatUtils.isRchatAdmin()) {
                agent.setGroups(groupService.findByAgent(agent));
            }
        }
        return agents;
    }

    @Override
    protected JpaRepository<Agent, String> repository() {
        return repository;
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public boolean hasAgents(Agent agent) {
        return repository.existsByParent(agent);
    }

    @Override
    @Cacheable(cacheNames = "rchat", key = "#root.args[0].id")
    public Optional<Agent> findByAdministrator(@SecurityResource User user) {
        return repository.findByAdministrator(user);
    }

    @Override
    public Page<Agent> findByParent(Agent parent, Pageable pageable) {
        return repository.findByParent(parent, pageable);
    }

    @Override
    public List<Agent> findByParent(Agent parent) {
        return repository.findByParent(parent);
    }

    @Override
    public Optional<Agent> findParent(Agent child) {
        return repository.findParent(child);
    }

    @Override
    public Page<Agent> search(Optional<String> agentName, Optional<String> adminName, Optional<String> adminUsername,
                              Pageable pageable) {

        // if (!agentName.isPresent() && !adminName.isPresent() &&
        // adminUsername.isPresent()) {
        // if (RchatUtils.isAgentAdmin()) {
        // return self.findByParent(env.currentAgent(), pageable);
        // } else if (RchatUtils.isRchatAdmin()) {
        // return self.findAll(pageable);
        // }
        // }
        //
        // if (agentName.isPresent()) {
        // return repository.findById(agentName.get(), pageable);
        // }
        //
        // if (adminName.isPresent() && adminUsername.isPresent()) {
        // return
        // repository.findByAdministratorUsernameLikeAndAdministratorNameLike(adminUsername.get(),
        // adminName.get(), pageable);
        // }
        //
        // if (adminUsername.isPresent()) {
        // return
        // repository.findByAdministratorUsernameLike(adminUsername.get(),
        // pageable);
        // }
        //
        // if (adminName.isPresent()) {
        // return repository.findByAdministratorNameLike(adminName.get(),
        // pageable);
        // }
        //
        // return new PageImpl<>(Collections.emptyList(), pageable, 0);

        return repository.search(agentName, adminName, adminUsername, pageable);
    }

    @Override
    public Optional<Agent> findByGroup(Group group) {
        return repository.findByGroup(group);
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Pageable pageable) {
        return repository.findBriefs(pageable);
    }

    @Override
    public List<Agent> findByLevel(Integer level) {
        return repository.findByLevel(level);
    }
}
