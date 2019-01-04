package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.domain.*;
import com.rchat.platform.domain.DepartmentPrivilege.OverLevelCallType;
import com.rchat.platform.exception.CannotDeleteExeption;
import com.rchat.platform.exception.SegmentExhaustionException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.GroupNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@SecurityService(ResourceType.GROUP)
public class GroupServiceImpl extends AbstractService<Group, String> implements GroupService {
    private static final Log log = LogFactory.getLog(GroupServiceImpl.class);

    @Autowired
    private GroupRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupCreditAccountService creditAccountService;
    @Autowired
    private GroupCreditOrderService creditOrderService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private SummaryService summaryService;
    @Autowired
    private AgentService agentService;
    
    @Autowired
    private JmsTemplate jms;

    @Override
    @Transactional
	@SecurityMethod(false)
    public Group update(Group entity) {
        userService.update(entity.getAdministrator());

        return super.update(entity);
    }
    
    @Override
    @SecurityMethod(false)
    public Optional<Group> findOne(String id) {
        Optional<Group> o = super.findOne(id);

        return o;
    }

    @Override
    @Transactional
    public Group create(Group group) {
        // 创建管理员
        User administrator = group.getAdministrator();
        administrator.setRoles(Collections.singletonList(new Role("a07bfa07-54c4-482d-9028-74cc9064edbc")));

        administrator = userService.create(administrator);
        group.setAdministrator(administrator);
        
        //设置默认的存储空间
        group.setStatus(false);
        group.setTotalSpace(512000);
        group.setUsedSpace(0);
        group.setDeadline(new Date(0));
        //根据代理商id获取代理商信息
        Agent agent=agentService.findOne(group.getAgent().getId()).get();
        //判断是否存在默认的代理商服务器ip
        if(ToolsUtil.isNotEmpty(agent)&&ToolsUtil.isNotEmpty(agent.getServerId())){
        	Server server=new Server();
        	server.setId(agent.getServerId());
        	group.setServer(server);
        	 // 创建集团
            group = super.create(group);
        }else{
        	 // 创建集团
            group = super.create(group);
        	 List<Server> servers = serverService.findAll();
             Server server = new Server();
             for (Server s : servers) {       	
             	if(s.getStatus() == ServerStatus.ONLINE){
             		server = s;
             		setServer(Collections.singletonList(group), server); 
             		group.setServer(server);
             		break;
             	}
             }
        }
              
        // 创建集团额度账户
        GroupCreditAccount account = new GroupCreditAccount();
        account.setGroup(group);

        CreditAccount a = creditAccountService.create(account);
        log.debug(a);

        // 默认号段
        GroupSegment segment = group.getSegment();
        segment.setGroup(group);

        AgentSegment agentSegment = segment.getAgentSegment();

        List<GroupSegment> existsSegments = groupSegmentService.findByAgentSegment(agentSegment);
        int existsSegmentValue = existsSegments.parallelStream().mapToInt(Segment::getValue).min().orElse(0);

        int value = existsSegmentValue / 100 * 100 + 1;
        do {
            if (!groupSegmentService.existsByAgentSegmentAndValue(agentSegment, value))
                break;
        } while (++value < 10000);

        if (value == 10000) {
            throw new SegmentExhaustionException();
        }

        segment.setValue(value);
        segment.setInternal(true);

        segment = groupSegmentService.create(segment);

        group.setSegment(segment);

        //创建集团默认部门和默认集群
//        Department department = new Department();
//        Group gp = findOne(group.getId()).orElseThrow(GroupNotFoundException::new);
//        gp.setServer(server);
//        User user = new User();
//        Role role = null;
//        role = new Role("e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0");
//        user.setRoles(Arrays.asList(role));
//        user.setName(String.format("%sdefault", group.getAdministrator().getName()));
//        user.setPassword("00000000");
//        user.setUsername(String.format("%sdefault", group.getAdministrator().getUsername()));
//        DepartmentPrivilege dp = new DepartmentPrivilege();
//        dp.setGpsEnabled(false);
//        dp.setOverGroupEnabled(false);
//        dp.setOverLevelCallType(OverLevelCallType.DISABLED);
//        department.setPrivilege(dp);
//        department.setAdministrator(user);
//        department.setGroup(gp);
//        department.setName(group.getName());
//        departmentService.create(department);
//        jms.convertAndSend(TopicNameConstants.DEPARTMENT_CREATE, department);
//        jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_CREATE, department.getDefaultGroup());
        //发送新增集团信息
        jms.convertAndSend(TopicNameConstants.GROUP_CREATE, group);
        return group;
    }

    @Override
    public Page<Group> findByAgent(Agent agent, Pageable pageable) {
        return repository.findByAgent(agent, pageable);
    }

    @Override
    public boolean delete(Group group) {
        assertCanDelete(group);

        groupSegmentService.deleteByGroup(group);
        creditAccountService.deleteByGroup(group);
        creditOrderService.deleteByGroup(group);

        return super.delete(group);
    }

    /**
     * 断言此集团可以删除，如果不能删除，则抛出异常；只有恢复道最初状态的集团能删除，即只有默认号段，且默认号段下没有对讲账号，其他的约束由外键自行判断
     *
     * @param group 待删除的集团
     */
    private void assertCanDelete(Group group) {
        // 查看是否存在非默认号段，如果存在，不能删除
        if (groupSegmentService.existsNonInternalSegments(group))
            throw new CannotDeleteExeption("存在非默认号段，不能删除");

        GroupSegment segment = groupSegmentService.getInternalSegment(group);
        if (talkbackUserService.existsBySegment(segment))
            throw new CannotDeleteExeption("默认账号中，还存在对讲账号，不能删除");
    }

    @Override
    protected JpaRepository<Group, String> repository() {
        return repository;
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public boolean hasGroups(Agent agent) {
        return repository.existsByAgent(agent);
    }

    @Override
    @Cacheable(cacheNames = "rchat", key = "#root.args[0].id")
    public Optional<Group> findByAdministrator(User user) {
        return repository.findByAdministrator(user);
    }

    @Override
    public List<Group> findByAgent(Agent agent) {
        List<Group> groups = repository.findByAgent(agent);

        if (RchatUtils.isAgentAdmin()) {
            for (Group group : groups) {
                group.setDepartments(departmentService.findByGroup(group));
            }
        }

        return groups;
    }

    @Override
    public Optional<Group> findByIdAndAgent(String id, Agent agent) {
        return repository.findByIdAndAgent(id, agent);
    }

    @Override
    public Page<Group> search(Optional<String> agentId, Optional<String> name, Optional<String> aName,
                              Optional<String> aUsername, Optional<Boolean> status, Pageable pageable) {

        Page<Group> groups = repository.search(agentId, name, aName, aUsername, status, pageable);
        groups.forEach(group -> {
            Summary summary = summaryService.count(group);

            group.setUserAmount(summary.getUserAmount());
            group.setNonactiveUserAmount(summary.getNonactiveUserAmount());
            group.setExpiredUserAmount(summary.getExpiredUserAmount());
            group.setExpiringUserAmount(summary.getExpiringUserAmount());
            group.setCreditAccumulation(summary.getCreditAccumulation());
            group.setCreditRemaint(summary.getCreditRemaint());
        });
        return groups;
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Pageable pageable) {
        return repository.findBriefs(null, pageable);
    }

    @Override
    @Transactional
    public void setServer(List<Group> groups, Server server) {
        List<String> groupIds = groups.parallelStream().map(Group::getId).collect(Collectors.toList());
        repository.setServer(groupIds, server);
    }

    @Override
    public boolean existsServer(Group group) {
        return repository.existsByIdAndServerNotNull(group.getId());
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        return repository.findBriefs(server, pageable);
    }
}
