package com.rchat.platform.web.controller.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.config.ServerProperties;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Business;
import com.rchat.platform.domain.BusinessRent;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.Summary;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.TalkbackGroupType;
import com.rchat.platform.domain.TalkbackNumber;
import com.rchat.platform.domain.TalkbackRole;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.domain.TalkbackUserRenewLog;
import com.rchat.platform.domain.User;
import com.rchat.platform.domain.sdk.UidInfo;
import com.rchat.platform.exception.NoDepartmentException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.DepartmentService;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.ServerService;
import com.rchat.platform.service.SummaryService;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.service.TalkbackNumberService;
import com.rchat.platform.service.TalkbackUserRenewLogService;
import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.service.UserService;
import com.rchat.platform.service.redis.RedisService;
import com.rchat.platform.service.sdk.UidInfoService;
import com.rchat.platform.web.controller.sdk.dto.LoginDto;
import com.rchat.platform.web.controller.sdk.dto.TokenDto;
import com.rchat.platform.web.exception.DataInputInvalidException;
import com.rchat.platform.web.exception.DepartmentNotFoundException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.exception.LevelDeepException;
import com.rchat.platform.web.exception.NoAvailableNumberException;
import com.rchat.platform.web.exception.NumberInsufficiencyException;
import com.rchat.platform.web.exception.SegmentNotFoundException;
import com.rchat.platform.web.exception.TalkbackGroupNotFoundException;
import com.rchat.platform.web.exception.TalkbackUserNotFoundException;
import com.rchat.platform.web.exception.UnknownActionException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api("SDKAPI接口模块")
@RestController
@RequestMapping("/sdk")
public class SdkApiController {
	@Autowired
	private UidInfoService uidInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private JmsTemplate jms;
	@Autowired
	private TalkbackGroupService talkbackGroupService;
	@Autowired
	private SummaryService summaryService;
	@Autowired
	private TalkbackUserService talkbackUserService;
	@Autowired
	private GroupSegmentService groupSegmentService;
	 /**
     * 随机生成
     */
    private static final int STRATEGY_RANDOM = 0;
    /**
     * 顺序生成
     */
    private static final int STRATEGY_SEQUENCE = 1;
    @Autowired
    private ServerService serverService;
    @Autowired
    private TalkbackNumberService talkbackNumberService;
    @Autowired
    private ServerProperties serverProperties;
    @Autowired
    private TalkbackUserRenewLogService talkbackUserRenewLogService;
	
	private static final Md5PasswordEncoder md5 = new Md5PasswordEncoder();
	@ApiOperation("根据uid授权获取token")
	@RequestMapping(value={"authorize"}, method={RequestMethod.GET})
	public Result authorize(String uid){
		Result result=new Result();
		TokenDto tokenDto = new TokenDto();
		Object oldToken=redisService.get(uid);
		if(ToolsUtil.isNotEmpty(oldToken)){
			tokenDto.setCode(7200);
			tokenDto.setUid(uid);
			tokenDto.setToken(oldToken.toString());
			result.setCode(0);
			result.setData(tokenDto);
			result.setMsg("授权获取token成功");
			return result;
		}
		List<UidInfo> uidInfos=uidInfoService.getbByUid(uid);
		if(ToolsUtil.isNotEmpty(uidInfos)&&uidInfos.size()==1){
			String token=UUID.randomUUID().toString().replace("-", "");
			redisService.set(uid, token, 3600);
			tokenDto.setCode(7200);
			tokenDto.setUid(uid);
			tokenDto.setToken(token);
			result.setCode(7200);
			result.setData(tokenDto);
			result.setMsg("授权获取token成功");
			return result;
		}else{
			result.setCode(-1);
			result.setMsg("该uid不存在");
		}
		return null;
	}
	@ApiOperation("根据uid,token,账号，用户名密码登录")
	@RequestMapping(value={"login"})
	public Result login(@RequestBody LoginDto loginDto){
		Result result=new Result();
		if(ToolsUtil.isNotEmpty(loginDto)){
			Object oldToken=redisService.get(loginDto.getUid());
			if(ToolsUtil.isNotEmpty(oldToken)){
				if(oldToken.toString().equals(loginDto.getToken())){
					Optional<User> optional = userService.findByUsername(loginDto.getUserName());
					User user=optional.get();
					boolean b=md5.isPasswordValid(user.getPassword(),loginDto.getPassword(),
							user.getSalt());
					if(b){
						List<Group> groups=groupService.findByUserId(user.getId());
						if(ToolsUtil.isNotEmpty(groups)){
							redisService.set(loginDto.getUid()+"s", loginDto.getUserName());
							Group group=groups.get(0);
							loginDto.setGroupId(group.getId());
							result.setCode(0);
							result.setMsg("登录成功");
							result.setData(loginDto);
							return result;
						}else{
							result.setCode(-5);
							result.setMsg("该账号不是集团账号");
							return result;
						}
					
					}else{
						result.setCode(-4);
						result.setMsg("用户名密码不正确");
						return result;
					}
				}else{
					result.setCode(-3);
					result.setMsg("该uid和token不匹配");
					return result;
				}
				
			}else{
				result.setCode(-2);
				result.setMsg("token不存在或已过期");
				return result;
			}
		}
		result.setMsg("登录失败");
		result.setCode(-500);
		return result;
	}
	@SecurityMethod(false)
	@ApiOperation("分页获取部门列表")
	@RequestMapping(value={"getDepmartPage"}, method={RequestMethod.GET})
	public Result getDepmartPage(@RequestParam Optional<String> groupId,
										   @RequestParam Optional<String> uid,
										   @RequestParam Optional<String> token,
                           				   @RequestParam Optional<String> departmentName, 
                           				   @RequestParam Optional<String> adminName,
                           				   @RequestParam Optional<String> adminUsername, 
                           				   @PageableDefault Pageable pageable){
		Result result=new Result();
		boolean flag=checkToken(uid.get(),token.get());
		if(flag){
			Optional<Group> group = groupService.findOne(groupId.get());
			Page<Department> page=departmentService.search(group.get(), departmentName, adminName, adminUsername, pageable);
			result.setCode(0);
			result.setData(page);
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
		
	}
	/**
	 * 检测token
	 * @param uid
	 * @param token
	 * @return
	 */
	private boolean checkToken(String uid, String token) {
		Object oldToken=redisService.get(uid);
		if(ToolsUtil.isNotEmpty(oldToken)){
			if(oldToken.toString().equals(token)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	@SecurityMethod(false)
	@ApiOperation("新增部门")
	@RequestMapping(value={"addDepmart"}, method={RequestMethod.POST})
	public Result addDepmart(@Validated @RequestBody Department department){
		Result result=new Result();
		boolean flag=checkToken(department.getUid(),department.getToken());
		if(flag){
			Object object=redisService.get(department.getUid()+"s");
			  Optional<User> optional = userService.findByUsername(object.toString());
			  User user=optional.get();
			  department.setCreator(user);
			  Group group = groupService.findOne(department.getGroup().getId()).orElseThrow(GroupNotFoundException::new);
		      department.setGroup(group);
		      	// 默认认为部门的level == 1
		        // 如果指定的父级部门，level就不能是1了，而是父级部门的level + 1
		        Optional.ofNullable(department.getParent()).ifPresent(p -> {
		            Department parent = departmentService.findOne(p.getId()).orElseThrow(DepartmentNotFoundException::new);
		            int level = parent.getLevel() + 1;
		            // 最多 10 级部门
		            if (level > 10) {
		                throw new LevelDeepException();
		            }
		            department.setParent(parent);
		            department.setLevel(level);
		        });
		      Department d = departmentService.create(department);
		      jms.convertAndSend(TopicNameConstants.DEPARTMENT_CREATE, d);
		      jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_CREATE, d.getDefaultGroup());
			result.setCode(0);
			result.setData(d);
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
		  
	}
	@SecurityMethod(false)
	@ApiOperation("修改部门")
	@RequestMapping(value={"updateDepmart"}, method={RequestMethod.GET})
	public Result updateDepmart(@RequestBody Department department){
		Result result=new Result();
		boolean flag=checkToken(department.getUid(),department.getToken());
		if(flag){
			department = departmentService.update(department);
			result.setCode(0);
			result.setData(department);
			jms.convertAndSend(TopicNameConstants.DEPARTMENT_UPDATE, department);
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	      

	}
	@SecurityMethod(false)
	@ApiOperation("删除部门")
	@RequestMapping(value={"deleteDepmart"}, method={RequestMethod.GET})
	public Result deleteDepmart(String id,String uid,String token){
		Result result=new Result();
		boolean flag=checkToken(uid,token);
		if(flag){
			Optional<Department> o = departmentService.findOne(id);
	        Department department = o.orElseThrow(DepartmentNotFoundException::new);
	        //需求变更: 当部门只剩下默认群组,并且对讲账户为0的时候,可以同时删除部门和对讲群组
	        TalkbackGroup talkbackGroup = talkbackGroupService.findByDepartmentAndType(department, TalkbackGroupType.DEFAULT).orElseThrow(TalkbackGroupNotFoundException::new);
	        Summary summary = summaryService.count(department);
	        long amount = summary.getUserAmount();
	        if(amount == 0){
	        	talkbackGroupService.delete(talkbackGroup);
	        	departmentService.delete(department);
	        	jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_DELETE, talkbackGroup);
	            jms.convertAndSend(TopicNameConstants.DEPARTMENT_DELETE, department);
	        } else {        	
	        	departmentService.delete(department);
	        }
			result.setCode(0);
			result.setMsg("删除成功");
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
		  
	}
	@SecurityMethod(false)
	@ApiOperation("分页获取群组列表")
	@RequestMapping(value={"getTalkBackGroupPage"}, method={RequestMethod.GET})
	public Result getTalkBackGroupPage(@PageableDefault Pageable pageable,String uid,String token){
		Result result=new Result();
		boolean flag=checkToken(uid,token);
		if(flag){
			Page<TalkbackGroup> page = talkbackGroupService.findAll(pageable);
			result.setCode(0);
			result.setData(page);
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	}
	@SecurityMethod(false)
	@ApiOperation("新增群组")
	@RequestMapping(value={"addTalkBackGroup"}, method={RequestMethod.POST})
	public Result addTalkBackGroup(@Validated @RequestBody TalkbackGroup talkbackGroup){
		Result result=new Result();
		boolean flag=checkToken(talkbackGroup.getUid(),talkbackGroup.getToken());
		if(flag){
			Optional<Group> o = groupService.findOne(talkbackGroup.getGroup().getId());

			talkbackGroup.setGroup(o.orElseThrow(GroupNotFoundException::new));

	        Optional.ofNullable(talkbackGroup.getDepartment()).ifPresent(d -> {
	        	talkbackGroup.setType(TalkbackGroupType.DEPARTMENT);
	        	talkbackGroup.setDepartment(departmentService.findOne(talkbackGroup.getDepartment().getId())
	                    .orElseThrow(DepartmentNotFoundException::new));
	        });
	        
	        TalkbackGroup g = talkbackGroupService.create(talkbackGroup);
	        jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_CREATE, g);
			result.setCode(0);
			result.setData(g);
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
		  
	}
	@SecurityMethod(false)
	@ApiOperation("修改群组")
	@RequestMapping(value={"updateTalkBackGroup"}, method={RequestMethod.POST})
	public Result updateTalkBackGroup(@Validated @RequestBody TalkbackGroup group){
		Result result=new Result();
		boolean flag=checkToken(group.getUid(),group.getToken());
		if(flag){
			assertTalkbackGroupExists(group.getId());
			group = talkbackGroupService.update(group);
			jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_UPDATE, group);
			result.setCode(0);
			result.setData(group);
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	}
	private void assertTalkbackGroupExists(String id) {
		boolean exists = talkbackGroupService.exists(id);
		if (!exists)
			throw new TalkbackGroupNotFoundException();
	}
	@SecurityMethod(false)
	@ApiOperation("删除群组")
	@RequestMapping(value={"deleteTalkBackGroup"}, method={RequestMethod.GET})
	public Result deleteTalkBackGroup(String id,String uid,String token){
		Result result=new Result();
		boolean flag=checkToken(uid,token);
		if(flag){
			Optional<TalkbackGroup> o = talkbackGroupService.findOne(id);
			TalkbackGroup group = o.orElseThrow(TalkbackGroupNotFoundException::new);
			talkbackGroupService.delete(group);
			jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_DELETE, group);
			result.setCode(0);
			result.setMsg("删除成功");
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	}
	@SecurityMethod(false)
	@ApiOperation("账号分页查询")
	@RequestMapping(value={"getTalkBackUserPage"}, method={RequestMethod.GET})
	public Result getTalkBackUserPage(Optional<String> fullValue, Optional<TalkbackRole> role,
									Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated,
					                Optional<String> departmentId, Optional<String> groupId, Optional<Date> createdStart,
					                Optional<Date> createdEnd, Optional<Date> renewStart,
					                Optional<Date> renewEnd,String uid,String token, @PageableDefault Pageable pageable){
	    
		Result result=new Result();
			boolean flag=checkToken(uid,token);
			if(flag){
				   Optional<Group> group = Optional.empty();
			        Optional<Agent> agent = Optional.empty();
			        Optional<Department> department = Optional.empty();
			        group = groupService.findOne(groupId.get());
			        agent = Optional.of(group.get().getAgent());
			        department = departmentService.findOne(departmentId.get());
			        Page<TalkbackUser> page=talkbackUserService.search(fullValue, role, shortValue, name, activated, department, group, agent, createdStart, createdEnd,
			                renewStart, renewEnd, pageable);
				result.setCode(0);
				result.setData(page);
				return result;
			}else{
				result.setCode(-7003);
				result.setMsg("token失效或不存在");
				return result;
			}
	}
	
	@SecurityMethod(false)
	@ApiOperation("新增账号")
	@RequestMapping(value={"addTalkBackUser"}, method={RequestMethod.POST})
	public Result addTalkBackUser(@RequestParam int count, @RequestParam int strategy,
            @RequestParam(defaultValue = "4") int shortValueLength, @Validated @RequestBody TalkbackUser user){
		Result result=new Result();
		boolean flag=checkToken(user.getUid(),user.getToken());
		if(flag){
		    if (shortValueLength < 4 || shortValueLength > 6) {
	            throw new DataInputInvalidException();
	        }

	        // 对讲账号一定是属于集团的        
	        setServerIfNecessary(user.getGroup(), count);
	        Group group = user.getGroup();
	        Optional<Server> servers = serverService.findByGroup(group);
	        servers.ifPresent(user.getGroup()::setServer);
	        // 取出集团所有号段
	        List<GroupSegment> segments = groupSegmentService.findByGroup(group);
	        Optional<GroupSegment> groupSegment = groupSegmentService.findOne(user.getNumber().getGroupSegment().getId());

	        boolean contains = segments.contains(groupSegment);
	        if (!contains) {
	            throw new SegmentNotFoundException();
	        }

	        user.getNumber().setGroupSegment(groupSegment.get());

	        // 如果指定的部门，则创建对讲账号归入部门的默认群组中。
	        setDefaultTalkbackGroup(user);

	        // 集团的所有对讲号码
//	        List<TalkbackNumber> numbers = talkbackNumberService.findByGroup(group);
//	        List<Integer> _unavailableNumbers = talkbackNumberService.findNumberValuesByGroup(group);
	        List<Integer> unavailableNumbers = talkbackNumberService.findNumberValuesByGroup(group); //numbers.parallelStream().map(TalkbackNumber::getValue).collect(Collectors.toList());

	        List<Integer> availableNumbers = new ArrayList<>();

	        // 终结号码
	        int end;

	        if (group.isVip()) {
	            end = 1000000;
	        } else {
	            end = 10000;
	        }
	        for (int n = 1; n < end; n++) {
	            availableNumbers.add(n);
	        }

	        // 剔除不可用的号码
	        availableNumbers.removeAll(unavailableNumbers);

	        if (availableNumbers.isEmpty()) {
	            throw new NoAvailableNumberException();
	        }

	        if (availableNumbers.size() < count) {
	            throw new NumberInsufficiencyException();
	        }

	        List<TalkbackUser> users = new ArrayList<>();

	        switch (strategy) {
	            case STRATEGY_RANDOM:
	                for (int i = 0; i < count; i++) {
	                    int index = RandomUtils.nextInt(0, availableNumbers.size());
	                    Integer value = availableNumbers.remove(index);

	                    TalkbackNumber number = new TalkbackNumber();
	                    number.setValue(value);
	                    number.setGroupSegment(groupSegment.get());

	                    TalkbackUser u = new TalkbackUser(user);
	                    u.setNumber(number);
	                    u.setDeadline(new Date(0));
	                    users.add(u);
	                }
	                break;
	            case STRATEGY_SEQUENCE:
	                for (int i = 0; i < count; i++) {
	                    Integer value = availableNumbers.get(i);

	                    TalkbackNumber number = new TalkbackNumber();
	                    number.setValue(value);
	                    number.setGroupSegment(groupSegment.get());

	                    TalkbackUser u = new TalkbackUser(user);
	                    u.setNumber(number);
	                    u.setDeadline(new Date(0));
	                    users.add(u);
	                }
	                break;
	            default:
	                break;
	        }

	        // 如果没有指定密码
	        if (user.getPassword() == null) {
	            users.parallelStream().forEach(u -> u.setPassword(u.getNumber().getFullValue()));
	        }
	        // 如果没有指定名称
	        if (user.getName() == null)
	            users.parallelStream().forEach(u -> u.setName(u.getNumber().getFullValue()));
	        //判断不存在部门
	        if(ToolsUtil.isEmpty(user.getDepartment())){
	        	throw new NoDepartmentException();
	        }
	        
	        users.parallelStream().forEach(u -> setShortValue(u.getNumber(), shortValueLength));
	        
	        Optional<TalkbackGroup> o = talkbackGroupService.findByDepartmentAndType(user.getDepartment(), TalkbackGroupType.DEFAULT);
	        TalkbackGroup g = o.orElseThrow(TalkbackGroupNotFoundException::new);
	        users.parallelStream().forEach(u -> u.setTalkbackGroupId(g.getId()));
	        users.parallelStream().forEach(newu -> newu.setFullValue(newu.getNumber().getFullValue()));
	        List<TalkbackUser> list = talkbackUserService.create(users);
	        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_CREATE, list);
			result.setCode(0);
			result.setData(list);
			result.setMsg("新增成功");
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	}
    private void setShortValue(TalkbackNumber number, int shortValueLength) {
        String fullValue = number.getFullValue();

        number.setShortValue(StringUtils.substring(fullValue, -shortValueLength));
    }

    /**
     * 设置默认对讲群组
     *
     * @param user 对讲账号
     */
    private void setDefaultTalkbackGroup(@Validated @RequestBody TalkbackUser user) {
        Optional.ofNullable(user.getDepartment()).ifPresent(d -> {
            Optional<TalkbackGroup> o = talkbackGroupService.findByDepartmentAndType(d, TalkbackGroupType.DEFAULT);
            TalkbackGroup g = o.orElseThrow(TalkbackGroupNotFoundException::new);

            List<TalkbackGroup> groups = Optional.ofNullable(user.getGroups()).orElse(new ArrayList<>());
            groups.add(g);

            user.setGroups(groups);
        });
    }
    
    /**
     * 看看需不需要设置集团的语音服务器
     *
     * @param group          指定集团
     * @param additionalSize 新增对讲用户数量
     */
    private void setServerIfNecessary(Group group, long additionalSize) {
        // 如果已经分配了语音服务器，就不管了，如果没有分配，则想办法分配
        if (!groupService.existsServer(group)) {
            Optional<Server> server = getServer();
            // 如果有服务器，则只会返回一个服务器
            server.ifPresent(s -> {
                if (canAllocate(additionalSize, s)) {
                    allocateServer(group, s);
                }
            });
        }
    }
    /**
     * 查询语音服务器
     *
     * @return 剩余容量最多的语音服务器分
     */
    private Optional<Server> getServer() {
        List<Server> servers = serverService.findAll();
        return servers.parallelStream().map(this::setRemanetCapacity).max(this::compareServer);
    }
    /**
     * 设置剩余容量
     *
     * @param server 语音服务器
     * @return 带剩余容量的语音服务器
     */
    private Server setRemanetCapacity(Server server) {
        server.setRemanentCapacity(server.getMaxCapacity() - talkbackUserService.countByServer(server));
        return server;
    }
    /**
     * 返回最大剩余容量的服务器
     *
     * @param s1 服务器
     * @param s2 服务器
     * @return 比较结果
     */
    private int compareServer(Server s1, Server s2) {
        // == -1 表示还没有统计结果
        return Long.compare(s1.getRemanentCapacity(), s2.getRemanentCapacity());
    }
    /**
     * 分配服务器
     *
     * @param group  未分配语音服务器的集团
     * @param server 语音服务器
     */
    private void allocateServer(Group group, Server server) {
        groupService.setServer(Collections.singletonList(group), server);
    }

    /**
     * 判断是否可以分配服务器
     *
     * @param additionalSize 新增对讲账号数量
     * @param server         语音服务器
     * @return 如果能分配，返回<code>true</code>，否则，返回<code>false</code>
     */
    private boolean canAllocate(long additionalSize, Server server) {
        // 获取当前服务器对讲账号总数
        long remanentCapacity = server.getRemanentCapacity();
        long maxCapacity = server.getMaxCapacity();
        // 如果小于服务器 容量上限百分比
        return maxCapacity > 0 &&
                (remanentCapacity - additionalSize + 0.0) / maxCapacity >= (1 - serverProperties.getThresholdRate());
    }
    @SecurityMethod(false)
	@ApiOperation("修改账号")
	@RequestMapping(value={"updateTalkBackUser"}, method={RequestMethod.POST})
	public Result updateTalkBackUser(@Validated @RequestBody TalkbackUser user){
		Result result=new Result();
		boolean flag=checkToken(user.getUid(),user.getToken());
		if(flag){
			 assertTalkbackUserExists(user.getId());
		     user = talkbackUserService.update(user);
		     jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, Collections.singletonList(user));
			result.setCode(0);
			result.setData(user);
			result.setMsg("修改成功");
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	}
	  private void assertTalkbackUserExists(String id) {
	        boolean exists = talkbackUserService.exists(id);
	        if (!exists)
	            throw new TalkbackUserNotFoundException();
	    }
	  @SecurityMethod(false)
	@ApiOperation("删除账号")
	@RequestMapping(value={"deleteTalkBackUser"}, method={RequestMethod.GET})
	public Result deleteTalkBackUser(String uid,String token,String id){
		Result result=new Result();
		boolean flag=checkToken(uid,token);
		if(flag){
			   Optional<TalkbackUser> o = talkbackUserService.findOne(id);
		        TalkbackUser user = o.orElseThrow(TalkbackUserNotFoundException::new);
		        //此处用于删除测试数据临时添加的功能,根据需求取消
		        if (RchatUtils.isRchatAdmin()) {
			        List<TalkbackUserRenewLog> list = talkbackUserRenewLogService.findRenewLogs(null, null, null, null, user.getNumber().getFullValue());
			        talkbackUserRenewLogService.delete(list);
		        }
		        talkbackUserService.delete(user);

		        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_DELETE, Collections.singletonList(user));
			result.setCode(0);
			result.setMsg("删除成功");
			return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
	}
	  @SecurityMethod(false)
	@PatchMapping(path = "/{id}", params = "action")
	@LogAPI("调度对讲群组")
	@ResponseStatus(code = HttpStatus.OK, reason = "调度对讲群组成功")
	public Result patch(@PathVariable String id,String uid,String token, String action, @RequestBody Optional<List<TalkbackUser>> users) {
		Result result=new Result();
		boolean flag=checkToken(uid,token);
		if(flag){
		TalkbackGroup group = talkbackGroupService.findOne(id).orElseThrow(TalkbackGroupNotFoundException::new);

		switch (action) {
		// 成员调度
		case "dispatch":
			group.setUsers(users.orElse(new ArrayList<TalkbackUser>()));
			talkbackGroupService.setTalbackUsers(group);
			break;
		default:
			throw new UnknownActionException();
		}

		jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_UPDATE, group);
		result.setCode(0);
		result.setMsg("调度成功");
		return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}

	}
	  @SecurityMethod(false)
	@PatchMapping("/batch-renew")
    @LogAPI("批量续费")
    @ResponseStatus(code = HttpStatus.OK, reason = "批量充值成功")
    public Result batchRenew(@RequestBody BatchRenewDto dto) {
		Result result=new Result();
		boolean flag=checkToken(dto.getUid(),dto.getToken());
		if(flag){
        List<TalkbackUser> users = talkbackUserService.findAll(dto.getTalkbackUserIds()).parallelStream()
                .filter(TalkbackUser::isEnabled).collect(Collectors.toList());
        List<BusinessRent> businessRents = dto.getBussinessRents().parallelStream().map(rent -> {
            BusinessRent businessRent = new BusinessRent();
            businessRent.setCreditMonths(rent.getCreditMonths());
            businessRent.setBusiness(new Business(rent.getBusinessId()));
            return businessRent;
        }).collect(Collectors.toList());
        talkbackUserService.batchRenew(users, businessRents);
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, users);
        result.setCode(0);
		result.setMsg("续费成功");
		return result;
		}else{
			result.setCode(-7003);
			result.setMsg("token失效或不存在");
			return result;
		}
    }
	
    private static class BatchRenewDto {
    	private String uid;
    	private String token;
        private List<String> talkbackUserIds;
        private List<BusinessRentDto> bussinessRents;
        public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public List<String> getTalkbackUserIds() {
            return talkbackUserIds;
        }

        public void setTalkbackUserIds(List<String> talkbackUserIds) {
            this.talkbackUserIds = talkbackUserIds;
        }

        public List<BusinessRentDto> getBussinessRents() {
            return bussinessRents;
        }

        public void setBussinessRents(List<BusinessRentDto> bussinessRents) {
            this.bussinessRents = bussinessRents;
        }
    }
    private static class BusinessRentDto {
        /**
         * 业务Id
         */
        private String businessId;
        /**
         * 充值数量
         */
        private Integer creditMonths;

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public Integer getCreditMonths() {
            return creditMonths;
        }

        public void setCreditMonths(Integer creditMonths) {
            this.creditMonths = creditMonths;
        }
    }
	@ApiOperation("测试")
	@RequestMapping(value={"test"}, method={RequestMethod.GET})
	public void test(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	
}
