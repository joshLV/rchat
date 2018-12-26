package com.rchat.platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit4.SpringRunner;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupScale;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.AgentSegmentService;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.web.security.SecurityUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RchatApplicationTests {

	@Autowired
	private TalkbackUserService talkbackUserService;
	@Autowired
	private TalkbackGroupService talkbackGroupService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private AgentSegmentService agentSegmentService;
	@Autowired
	private GroupSegmentService groupSegmentService;

	@BeforeClass
	public static void setup() throws Exception {
		SecurityContext context = new SecurityContextImpl();

		User user = new User();
		user.setId("aa6323f9-85aa-4b20-9372-5c58c86d6a89");
		user.setUsername("测试");
		user.setName("测试");
		user.setActivated(true);
		user.setEnabled(true);
		user.setExpired(false);

		user.setRoles(Arrays.asList(new Role("32b62a97-502f-4164-ab7d-1f3c50bad1e0")));

		Object securityUser = new SecurityUser(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, "password@rchat");
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	@Test
	public void testCreateGroups() throws Exception {
		List<Group> groups = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			Group group = new Group();
			Agent agent = new Agent("f1d31dfb-4056-482a-bcb6-35d86fed4017");
			group.setAgent(agent);

			group.setName(RandomStringUtils.randomAlphabetic(16));
			User administrator = new User();
			administrator.setRoles(Arrays.asList(new Role("a07bfa07-54c4-482d-9028-74cc9064edbc")));
			administrator.setUsername(RandomStringUtils.randomAlphabetic(32));
			administrator.setName(RandomStringUtils.randomAlphabetic(16));
			String salt = RandomStringUtils.randomAlphabetic(16);
			String password = RchatUtils.encodePassword("123456", salt);
			administrator.setPassword(password);
			administrator.setSalt(salt);
			Date now = new Date();
			administrator.setUpdatedAt(now);
			administrator.setCreatedAt(now);
			group.setScale(GroupScale.LARGE);
			group.setVip(false);
			group.setAdministrator(administrator);

			groups.add(group);
			if (i % 1000 == 0) {
				groupService.create(groups);
				groups.clear();
			}
		}
	}

	@Test
	public void testCreateTalkbackUser() throws Exception {
	}

	@Test
	public void testCreateGroupSegment() throws Exception {
		List<GroupSegment> segments = new ArrayList<>();

		for (int i = 28002; i < 100000; i++) {

			GroupSegment segment = new GroupSegment();
			Group group = new Group("d723b426-e8ab-4c3a-952b-15fbcace2042");
			segment.setGroup(group);
			segment.setVip(false);
			AgentSegment agentSegment = new AgentSegment();
			agentSegment.setId("f66099c9-938f-45b2-89a0-c41b19edc803");
			segment.setAgentSegment(agentSegment);
			segment.setValue(i + 1);

			segments.add(segment);
			if (segments.size() % 5000 == 0) {
				groupSegmentService.create(segments);
			}
		}
		Lock lock = new ReentrantLock();
		Condition c1 = lock.newCondition();
		c1.await();
	}

	@Test
	public void testCreateAgentSegment() throws Exception {
		AgentSegment segment = new AgentSegment();
		Agent agent = new Agent("f1d31dfb-4056-482a-bcb6-35d86fed4017");
		segment.setAgent(agent);
		segment.setValue(1);

		agentSegmentService.create(segment);
	}

	@Test
	public void contextLoads() {
		for (int i = 0; i < 10000; i++) {
			TalkbackGroup talkbackGroup =new TalkbackGroup();
			String id=UUID.randomUUID().toString();
			talkbackGroup.setId(id);
			talkbackGroup.setCreatedAt(new Date());
			talkbackGroup.setName("测试s"+i);
			talkbackGroup.setPriority(5);
			Department department=new Department();
			department.setId("8a5cea4c-35c7-4a23-9fdc-104815d57240");
			talkbackGroup.setDepartment(department);
			Group group=new Group();
			group.setId("e0680c58-69c8-4303-b67e-bbd08fe3bf5f");
			talkbackGroup.setGroup(group);
			talkbackGroupService.create(talkbackGroup);
			
		}
	}
	
	

}
