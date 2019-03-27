package com.rchat.platform.web.controller.sdk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.Summary;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.TalkbackGroupType;
import com.rchat.platform.domain.User;
import com.rchat.platform.domain.sdk.UidInfo;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.DepartmentService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.SummaryService;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.service.UserService;
import com.rchat.platform.service.redis.RedisService;
import com.rchat.platform.service.sdk.UidInfoService;
import com.rchat.platform.web.controller.sdk.dto.LoginDto;
import com.rchat.platform.web.controller.sdk.dto.TokenDto;
import com.rchat.platform.web.exception.DepartmentNotFoundException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.exception.IdNotMatchException;
import com.rchat.platform.web.exception.LevelDeepException;
import com.rchat.platform.web.exception.TalkbackGroupNotFoundException;

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
	@RequestMapping(value={"login"}, method={RequestMethod.POST})
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
						Optional<Group> optionals=groupService.findByAdministrator(user);
						if(ToolsUtil.isNotEmpty(optionals)){
							redisService.set(loginDto.getUid()+"s", loginDto.getUserName());
							loginDto.setGroupId(optionals.get().getId());
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
	@ApiOperation("分页获取群组列表")
	@RequestMapping(value={"getTalkBackGroupPage"}, method={RequestMethod.GET})
	public void getTalkBackGroupPage(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("新增群组")
	@RequestMapping(value={"addTalkBackGroup"}, method={RequestMethod.GET})
	public void addTalkBackGroup(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("修改群组")
	@RequestMapping(value={"updateTalkBackGroup"}, method={RequestMethod.GET})
	public void updateTalkBackGroup(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("删除群组")
	@RequestMapping(value={"deleteTalkBackGroup"}, method={RequestMethod.GET})
	public void deleteTalkBackGroup(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("账号分页查询")
	@RequestMapping(value={"getTalkBackUserPage"}, method={RequestMethod.GET})
	public void getTalkBackUserPage(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("新增账号")
	@RequestMapping(value={"addTalkBackUser"}, method={RequestMethod.GET})
	public void addTalkBackUser(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("修改账号")
	@RequestMapping(value={"updateTalkBackUser"}, method={RequestMethod.GET})
	public void updateTalkBackUser(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("删除账号")
	@RequestMapping(value={"deleteTalkBackUser"}, method={RequestMethod.GET})
	public void deleteTalkBackUser(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("群组增加账号")
	@RequestMapping(value={"talkBackGroupAddUser"}, method={RequestMethod.GET})
	public void talkBackGroupAddUser(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
	}
	@ApiOperation("群组删除账号")
	@RequestMapping(value={"talkBackGroupDeleteUser"}, method={RequestMethod.GET})
	public void talkBackGroupDeleteUser(){
		  String salt = RandomStringUtils.randomAlphanumeric(32);
		String password=md5.encodePassword("rchat@password", salt);
		boolean b=md5.isPasswordValid(password,"rchat@password",
				salt);
		System.out.println(b);
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
