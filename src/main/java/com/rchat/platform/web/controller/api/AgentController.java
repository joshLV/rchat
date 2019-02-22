package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.*;
import com.rchat.platform.web.format.AgentTypeFormatter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/agents")
@Api(value="代理商模块")
public class AgentController {
    @Autowired
    private RchatEnv env;
    @Autowired
    private AgentService agentService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private AgentCreditOrderService agentCreditOrderService;
    @Autowired
    private CreditOrderService creditOrderService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private AgentSegmentService agentSegmentService;
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private SummaryService summaryService;

    @Autowired
    private JmsTemplate jms;
    @Autowired
    private ServerService serverService;
    @Autowired
    private UserService userService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new AgentTypeFormatter());
    }

    @GetMapping("/{agentId}/agents")
    public Page<Agent> subAgents(@PathVariable String agentId, @PageableDefault Pageable pageable) {
        assertAgentExists(agentId);
        Agent agent = new Agent(agentId);

        return agentService.findByParent(agent, pageable);
    }

    @GetMapping("/{agentId}/groups")
    public Page<Group> groups(@PathVariable String agentId, @PageableDefault Pageable pageable) {
        assertAgentExists(agentId);
        Agent agent = new Agent(agentId);

        Page<Group> groups = groupService.findByAgent(agent, pageable);
        groups.forEach(this::setSummary);
        return groups;
    }

    private void setSummary(Group group) {
        Summary summary = summaryService.count(group);
        group.setCreditRemaint(summary.getCreditRemaint());
        group.setCreditAccumulation(summary.getCreditAccumulation());
        group.setUserAmount(summary.getUserAmount());
        group.setExpiringUserAmount(summary.getExpiringUserAmount());
        group.setExpiredUserAmount(summary.getExpiredUserAmount());
        group.setNonactiveUserAmount(summary.getNonactiveUserAmount());
    }

    @GetMapping("/{agentId}/groups/{groupId}")
    public Group group(@PathVariable String agentId, @PathVariable String groupId) {
        assertAgentExists(agentId);

        Optional<Group> oGroup = groupService.findOne(groupId);
        Group group = oGroup.orElseThrow(GroupNotFoundException::new);

        if (!group.getAgent().getId().equals(agentId)) {
            throw new AgentGroupNotMatchException();
        }

        return group;
    }

    @GetMapping("/{agentId}/groups/{groupId}/departments")
    public List<Department> treeDepartments(@PathVariable String agentId, @PathVariable String groupId,
                                            @RequestParam(defaultValue = "1") int depth) {
        assertAgentExists(agentId);
        Agent agent = new Agent(agentId);

        Optional<Group> og = groupService.findOne(groupId);
        Group group = og.orElseThrow(GroupNotFoundException::new);

        if (!agent.equals(group.getAgent())) {
            throw new AgentGroupNotMatchException();
        }

        if (depth <= 1) {
            List<Department> departments = departmentService.findByGroup(group);
            departments.parallelStream().forEach(d -> d.setChildren(Collections.emptyList()));
            return departments;
        } else {
            List<Department> departments = departmentService.findByGroup(group);

            departments.parallelStream().forEach(d -> d.setChildren(departmentService.findDepartmentTree(d, depth - 1)));

            return departments;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建代理商")
    public Agent create(@RequestBody Agent agent) {
        Agent parent = agent.getParent();

        // 如果是代理商，不能指定上级代理商，上级代理商只能是自己
        if (RchatUtils.isAgentAdmin()) {
            parent = env.currentAgent();
        } else if (RchatUtils.isRchatAdmin()) { // 如果是超级受理台，则可以自己是上级代理商，也可以指定上级代理商
            // 如果有指定上级代理商，则认为超级受理台自己就是上级代理商
            if (parent == null) {
                parent = env.currentAgent();
            } else {
                // 否则
                parent = agentService.findOne(parent.getId()).orElseThrow(AgentNotFoundException::new);
            }
        } else
            throw new DataInputInvalidException();

        int level = parent.getLevel() + 1;

        if (level > 5) {
            throw new LevelDeepException();
        }
        //判断是否存在服务器ip
        if(ToolsUtil.isNotEmpty(agent.getServerId())){
        	agent.setServerId(agent.getServerId());
        }

        agent.setLevel(level);
        agent.setParent(parent);
        agent.setCreator(RchatUtils.currentUser());
        

        // 如果level > 1 说明必须要指定上级代理商的号段，其实之后超级代理商才能创建 == 1 的代理商
        if (agent.getLevel() > 1) {
            AgentSegment segment = Optional.ofNullable(agent.getSegment()).orElseThrow(DataInputInvalidException::new);
            segment = agentSegmentService.findOne(segment.getId()).orElseThrow(SegmentNotFoundException::new);
            agent.setSegment(segment);
        }

        agent = agentService.create(agent);

        jms.convertAndSend(TopicNameConstants.AGENT_CREATE, agent);

        return agent;
    }

    @PostMapping("/{agentId}/segments")
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建代理商号段")
    public Segment createSegment(@PathVariable String agentId, @Validated @RequestBody AgentSegment segment) {
        assertAgentExists(agentId);

        segment.setAgent(new Agent(agentId));
        return agentSegmentService.create(segment);
    }

    @GetMapping("/{agentId}/segments")
    public Page<AgentSegment> segments(@PathVariable String agentId, @PageableDefault Pageable pageable) {
        assertAgentExists(agentId);

        return agentSegmentService.findByAgent(new Agent(agentId), pageable);
    }

    /**
     * 未实现
     */
    @GetMapping("/{agentId}/agent-segments")
    public Page<AgentSegment> agentSegments(@PathVariable String agentId, @PageableDefault Pageable pageable) {
        return new PageImpl<>(Collections.emptyList());
    }

    @GetMapping("/{agentId}/group-segments")
    public Page<GroupSegment> groupSegments(@PathVariable String agentId, @RequestParam(required = false) Boolean internal,
                                            @PageableDefault Pageable pageable) {
        Agent agent;
        if (RchatUtils.isAgentAdmin()) {
            agent = env.currentAgent();
        } else
            agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);

        if (Optional.ofNullable(internal).isPresent()) {
            return groupSegmentService.findGroupSegments(agent, internal, pageable);
        }

        return groupSegmentService.findGroupSegments(agent, pageable);

    }

    @GetMapping("/{agentId}/account")
    public CreditAccount account(@PathVariable String agentId) {
        assertAgentExists(agentId);
        Agent agent = new Agent(agentId);

        return agentCreditAccountService.findByAgent(agent);
    }

    /**
     * 查询指定代理商额度订单信息，代理商既可以申请额度，也可以下发额度，因此额度订单分多钟情况
     *
     * @param agentId  代理商id
     * @param type     订单类型： 0 全部订单，1. 待处理订单， 2. 收到的订单， 3. 发出的订单
     * @param pageable 分页信息
     * @return 订单列表
     */
    @GetMapping(path = "/{agentId}/orders", params = "type")
    public Page<? extends CreditOrder> orders(@PathVariable String agentId, @RequestParam int type,
                                              @PageableDefault Pageable pageable) {
        assertAgentExists(agentId);
        Agent agent = new Agent(agentId);

        switch (type) {
            case 0: // 全部订单
                return creditOrderService.findAll(agent, pageable);
            case 1: // 待处理订单
                return creditOrderService.findPendingOrders(agent, pageable);
            case 2: // 收到的订单
                return creditOrderService.findReceivedOrders(agent, pageable);
            case 3: // 申请的订单
                return agentCreditOrderService.findSentOrders(agent, pageable);
            default:
                throw new NoRightAccessException();
        }
    }

    @PostMapping("/{agentId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建代理商额度订单")
    public CreditOrder createOrder(@PathVariable String agentId, @Validated @RequestBody AgentCreditOrder order) {
        Optional<Agent> o = agentService.findOne(agentId);
        Agent agent = o.orElseThrow(AgentNotFoundException::new);

        order.setAgent(agent);
        order.setRespondent(agent.getParent());

        return agentCreditOrderService.create(order);
    }

    @DeleteMapping("/{id}")
    @LogAPI("删除代理商")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除代理商成功")
    public void delete(@PathVariable String id) {
        Agent parent = env.currentAgent();

        Optional<Agent> o = agentService.findOne(id);
        Agent agent = o.orElseThrow(AgentNotFoundException::new);

        // 直属关系才能删除
        if (!agent.getParent().equals(parent) && RchatUtils.isAgentAdmin()) {
            throw new AgentNotBelongToCurrentAgent();
        }

        boolean hasAgents = agentService.hasAgents(agent);
        // 不能删除有下级代理商的代理商
        if (hasAgents) {
            throw new TooManyAgentsException();
        }

        boolean hasGroups = groupService.hasGroups(agent);
        // 不能删除有集团用户的代理商
        if (hasGroups) {
            throw new TooManyGroupsException();
        }

        agentService.delete(agent);

        jms.convertAndSend(TopicNameConstants.AGENT_DELETE, agent);
    }

    @GetMapping("/{id}")
    public Agent detail(@PathVariable String id) {
        Agent agent = findOne(id).orElseThrow(AgentNotFoundException::new);
        setSummary(agent);

        return agent;
    }

    private Optional<Agent> findOne(String id) {
        if (RchatUtils.isRchatAdmin()) {
            return agentService.findOne(id);
        } else if (RchatUtils.isAgentAdmin()) {
            Agent parent = env.currentAgent();

            if (parent.getId().equals(id)) {
                return agentService.findOne(id);
            }

            return agentViewService.findOneUnderAgent(id, parent);
        } else {
            return Optional.empty();
        }
    }

    @GetMapping(path = "/{agentId}", params = "depth")
    public Agent tree(@PathVariable String agentId, @RequestParam Integer depth) {
        Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);

        agent.setChildren(agentService.findAgentTree(agent, depth));

        setSummary(agent);
        // 如果是超级受理台管理员，则可以查询代理商的集团信息
        // if (RchatUtils.isRchatAdmin()) {
        // agent.setGroups(groupService.findByAgent(agent));
        // }

        // setParentNull(agent);

        return agent;
    }

    private void setSummary(Agent agent) {
        if (agent == null)
            return;

        Summary summary = summaryService.count(agent);
        agent.setUserAmount(summary.getUserAmount());
        agent.setNonactiveUserAmount(summary.getNonactiveUserAmount());
        agent.setExpiredUserAmount(summary.getExpiredUserAmount());
        agent.setExpiringUserAmount(summary.getExpiringUserAmount());
        agent.setCreditRemaint(summary.getCreditRemaint());
        agent.setCreditAccumulation(summary.getCreditAccumulation());

        List<Agent> children = agent.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(this::setSummary);
        }
    }

    private void setParentNull(Agent agent) {
        if (agent != null) {
            agent.setParent(null);
            List<Agent> children = agent.getChildren();

            if (!CollectionUtils.isEmpty(children)) {
                children.parallelStream().forEach(this::setParentNull);
            }
        }
    }

    @GetMapping
    public List<Agent> tree(@RequestParam(required = false) Integer depth) {
        Agent agent = env.currentAgent();

        agent.setChildren(agentService.findAgentTree(agent, Optional.ofNullable(depth).orElse(3)));
        // 如果是超级受理台管理员，则可以查询代理商的集团信息
        if (RchatUtils.isRchatAdmin()) {
            agent.setGroups(groupService.findByAgent(agent));
        }

        setParentNull(agent);

        return Collections.singletonList(agent);
    }

    @PutMapping("/{id}")
    @LogAPI("更新代理商")
    public Agent update(@PathVariable String id, @RequestBody Agent agent) {
        assertAgentExists(id);

        if (!id.equals(agent.getId())) {
            throw new IdNotMatchException();
        }

        //判断是否存在服务器ip
        if(ToolsUtil.isNotEmpty(agent.getServerId())){
        	agent.setServerId(agent.getServerId());
        }
        agent = agentService.update(agent);
        jms.convertAndSend(TopicNameConstants.AGENT_UPDATE, agent);

        return agent;
    }

    @Autowired
    private AgentViewService agentViewService;

    @GetMapping("/views")
    public Page<AgentView> views(@PageableDefault Pageable pageable) {
        return agentViewService.findAll(pageable);
    }

    @GetMapping("/views/{parentId}")
    public Page<Agent> subAgentViews(@PathVariable String parentId, @PageableDefault Pageable pageable) {
        Agent parent = agentService.findOne(parentId).orElseThrow(AgentNotFoundException::new);
        return agentViewService.findChildren(parent, pageable);
    }

    private void assertAgentExists(String id) {
        boolean exists = agentService.exists(id);
        if (!exists)
            throw new AgentNotFoundException();
    }
    

    @ApiOperation("代理商个性化配置")
    @RequestMapping(value={"fileUpload"}, method={RequestMethod.POST})
    public Result list(@RequestParam(value = "file", required = true) MultipartFile file,HttpServletRequest request,@RequestParam(value="titleName")String titleName) throws IllegalStateException, IOException{
    	Result result=new Result();
    	Agent agent =new Agent();
    	String filePath = "";
    	if(RchatUtils.isAgentAdmin()){
    		User user=RchatUtils.currentUser();
    		Optional<User> optionals=userService.findByUsername(user.getUsername());
    		user=optionals.get();
    		Optional<Agent> optional=agentService.findByAdministrator(user);
    		agent=optional.get();
    		agent.setTitleName(titleName);
    		if (!file.isEmpty()){
    			//使用StreamsAPI方式拷贝文件
//    		    Streams.copy(file.getInputStream(),new FileOutputStream(filePath),true);
    			String realPath = request.getSession().getServletContext().getRealPath("/");
    			filePath = realPath+"/" + file.getOriginalFilename();
    			file.transferTo(new File(filePath));
    			agent.setLogPath(filePath);
    		}
    		agent=agentService.update(agent);
    		result.setCode(0);
    		result.setData(agent);
    	}else{
    		result.setCode(-1);
    		result.setMsg("没有权限");
    	}
      return result;
    }

}
