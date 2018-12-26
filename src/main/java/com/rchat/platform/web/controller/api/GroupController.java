package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
@Api(value="集团模块")
@RestController
@RequestMapping("/api/groups")
public class GroupController extends MessageListenerAdapter{
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupCreditAccountService creditAccountService;
    @Autowired
    private GroupCreditOrderService creditOrderService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private TalkbackGroupService talkbackGroupService;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private AgentSegmentService agentSegmentService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private RchatEnv env;
    @Autowired
    private JmsTemplate jms;
    @Autowired
    private SummaryService summaryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建集团")
    @ApiOperation(value = "新增集团")
    public Group create(@Validated @RequestBody Group group) {
        group.setCreator(RchatUtils.currentUser());

        Agent agent = agentService.findOne(group.getAgent().getId()).orElseThrow(AgentNotFoundException::new);

        group.setAgent(agent);
        GroupSegment segment = group.getSegment();
        if (segment == null) {
            throw new SegmentNotFoundException();
        }

        AgentSegment agentSegment = agentSegmentService.findOne(segment.getAgentSegment().getId())
                .orElseThrow(SegmentNotFoundException::new);
        group.getSegment().setAgentSegment(agentSegment);

        group = groupService.create(group);

        return group;
    }

    @GetMapping
    public Page<Group> list(@PageableDefault Pageable pageable) {
        Page<Group> groups;
        if (RchatUtils.isGroupAdmin()) {
            throw new NoRightAccessException();
        } else if (RchatUtils.isAgentAdmin()) {
            groups = groupService.findByAgent(env.currentAgent(), pageable);
        } else {
            groups = groupService.findAll(pageable);
        }
        groups.forEach(this::setSummary);

        return groups;
    }

    @PutMapping("/{id}")
    @LogAPI("更新集团")
    public Group update(@PathVariable String id, @RequestBody Group group) {
        assertGroupExists(id);

        group.setId(id);
        group = groupService.update(group);

        jms.convertAndSend(TopicNameConstants.GROUP_UPDATE, group);

        return group;
    }

    @DeleteMapping("/{id}")
    @LogAPI("删除集团")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除集团成功")
    public void delete(@PathVariable String id) {
        Optional<Group> o = groupService.findOne(id);

        Group group = o.orElseThrow(GroupNotFoundException::new);
        groupService.delete(group);

        jms.convertAndSend(TopicNameConstants.GROUP_DELETE, group);
    }

    @GetMapping("/{id}")
    public Group one(@PathVariable String id) {
        if (RchatUtils.isAgentAdmin()) {
            Optional<Group> o = groupService.findByIdAndAgent(id, env.currentAgent());
            return o.orElseThrow(NoRightAccessException::new);
        } else if (RchatUtils.isGroupAdmin()) {
//            Group group = env.currentGroup();
        	Optional<Group> o = groupService.findOne(id);
            if (o.isPresent()) {
                return o.orElseThrow(NoRightAccessException::new);
            } else {
                throw new NoRightAccessException();
            }
        }

        Optional<Group> o = groupService.findOne(id);
        o.ifPresent(this::setSummary);

        return o.orElseThrow(GroupNotFoundException::new);
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

    @GetMapping("/{groupId}/account")
    public CreditAccount account(@PathVariable String groupId) {
        assertGroupExists(groupId);

        return creditAccountService.findByGroup(new Group(groupId));
    }

    private void assertGroupExists(String groupId) {
        boolean exists = groupService.exists(groupId);
        if (!exists)
            throw new GroupNotFoundException();
    }

    @GetMapping("/{groupId}/orders")
    public Page<GroupCreditOrder> orders(@PathVariable String groupId, @PageableDefault Pageable pageable) {
        assertGroupExists(groupId);

        return creditOrderService.findByGroup(new Group(groupId), pageable);
    }

    @PostMapping("/{groupId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建集团额度订单")
    public CreditOrder createOrder(@PathVariable String groupId, @Validated @RequestBody GroupCreditOrder order) {
        Optional<Group> o = groupService.findOne(groupId);
        Group group = o.orElseThrow(GroupNotFoundException::new);

        order.setGroup(group);
        if (order.getRespondent() == null) {
            order.setRespondent(group.getAgent());
        }

        return creditOrderService.create(order);
    }

    @PostMapping("/{groupId}/segments")
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建集团号段")
    public GroupSegment createSegment(@PathVariable String groupId, @Validated @RequestBody GroupSegment segment) {
        assertGroupExists(groupId);

        segment.setGroup(new Group(groupId));
        return groupSegmentService.create(segment);
    }

    @GetMapping("/{groupId}/segments")
    public Page<GroupSegment> segments(@PathVariable String groupId, @PageableDefault Pageable pageable) {
        assertGroupExists(groupId);

        return groupSegmentService.findByGroup(new Group(groupId), pageable);
    }

    @GetMapping("/{groupId}/departments")
    public List<Department> departments(@PathVariable String groupId, @RequestParam(defaultValue = "2") int depth) {
        assertGroupExists(groupId);

        Group group = new Group(groupId);

        List<Department> departments = departmentService.findByGroupDirectlyUnder(group);

        for (Department department : departments) {
            department.setChildren(departmentService.findDepartmentTree(department, depth));
        }

        // departments.parallelStream().forEach(d -> setParentNull(d));

        return departments;
    }

    @PostMapping("/{groupId}/talkback-users")
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建对讲账号")
    public TalkbackUser createTalkbackUser(@PathVariable String groupId, @Validated @RequestBody TalkbackUser user) {
        if (!groupId.equals(user.getGroup().getId())) {
            throw new IdNotMatchException();
        }

        assertGroupExists(groupId);

        return talkbackUserService.create(user);
    }

    @GetMapping("/{groupId}/talkback-users")
    public Page<TalkbackUser> talkbackUsers(@PathVariable String groupId, @PageableDefault Pageable pageable) {
        assertGroupExists(groupId);

        return talkbackUserService.findByGroup(new Group(groupId), pageable);
    }

    @PostMapping("/{groupId}/talkback-groups")
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建对讲群组")
    public TalkbackGroup createTalkbackGroup(@PathVariable String groupId,
                                             @Validated @RequestBody TalkbackGroup group) {
        if (!groupId.equals(group.getGroup().getId())) {
            throw new IdNotMatchException();
        }

        Optional<Group> o = groupService.findOne(groupId);

        group.setGroup(o.orElseThrow(GroupNotFoundException::new));

        Optional.ofNullable(group.getDepartment()).ifPresent(d -> {
            group.setType(TalkbackGroupType.DEPARTMENT);
            group.setDepartment(departmentService.findOne(group.getDepartment().getId())
                    .orElseThrow(DepartmentNotFoundException::new));
        });

        TalkbackGroup g = talkbackGroupService.create(group);
        jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_CREATE, g);

        return g;
    }

    @GetMapping("/{groupId}/talkback-groups")
    public Page<TalkbackGroup> talkbackGroups(@PathVariable String groupId, @PageableDefault Pageable pageable) {
        assertGroupExists(groupId);

        return talkbackGroupService.findByGroup(new Group(groupId), pageable);
    }
    
    //监听注解
    @JmsListener(destination = "rchat.topic.group-space.updateOne")
	public void onMessageheheOne(Message message, Session session) throws JMSException, JSONException {
		try {
			updateSpace(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    //监听注解
    @JmsListener(destination = "rchat.topic.group-space.updateTwo")
	public void onMessageheheTwo(Message message, Session session) throws JMSException, JSONException {
		try {
			updateSpace(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    //监听注解
    @JmsListener(destination = "rchat.topic.group-space.updateThree")
	public void onMessageheheThree(Message message, Session session) throws JMSException, JSONException {
		try {
			updateSpace(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    //监听注解
    @JmsListener(destination = "rchat.topic.group-space.updateFour")
	public void onMessageheheFour(Message message, Session session) throws JMSException, JSONException {
		try {
			updateSpace(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    //监听注解
    @JmsListener(destination = "rchat.topic.group-space.updateFive")
	public void onMessageheheFive(Message message, Session session) throws JMSException, JSONException {
		try {
			updateSpace(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}

	private void updateSpace(Message message) throws JMSException, JSONException {
		String str = (String)getMessageConverter().fromMessage(message);
		JSONObject json=new JSONObject(str);
		Map<String,Object> map=new HashMap<String, Object>();
		Iterator it = json.keys();
		while (it.hasNext()) {  
		   String key = (String) it.next();  
		   Object value = json.get(key);  
		   map.put(key, value);
		}
		String gId = map.get("groupId")!=null?map.get("groupId").toString() : "";
		String usedSpace = map.get("usedSpace")!=null?map.get("usedSpace").toString() : "0";
		if(gId.length()>0){
			Optional<Group> group = groupService.findOne(gId);
			if(group.isPresent()){
				Group gp = group.get();
				gp.setUsedSpace(Long.parseLong(usedSpace));
				groupService.update(gp);
			}
		}
	}
}
