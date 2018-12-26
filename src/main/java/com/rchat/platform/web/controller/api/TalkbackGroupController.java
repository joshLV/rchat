package com.rchat.platform.web.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.web.exception.TalkbackGroupNotFoundException;
import com.rchat.platform.web.exception.UnknownActionException;

@RestController
@RequestMapping("/api/talkback-groups")
public class TalkbackGroupController {
	@Autowired
	private TalkbackGroupService talkbackGroupService;
	@Autowired
	private TalkbackUserService talkbackUserService;

	@Autowired
	private JmsTemplate jms;

	private void assertTalkbackGroupExists(String id) {
		boolean exists = talkbackGroupService.exists(id);
		if (!exists)
			throw new TalkbackGroupNotFoundException();
	}

	@PatchMapping(path = "/{id}", params = "action")
	@LogAPI("调度对讲群组")
	@ResponseStatus(code = HttpStatus.OK, reason = "调度对讲群组成功")
	public void patch(@PathVariable String id, String action, @RequestBody Optional<List<TalkbackUser>> users) {
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

	}

	@DeleteMapping("/{id}")
	@LogAPI("删除对讲群组")
	@ResponseStatus(code = HttpStatus.OK, reason = "删除对讲群组成功")
	public void delete(@PathVariable String id) {
		Optional<TalkbackGroup> o = talkbackGroupService.findOne(id);
		TalkbackGroup group = o.orElseThrow(TalkbackGroupNotFoundException::new);

		talkbackGroupService.delete(group);

		jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_DELETE, group);
	}

	@PutMapping("/{id}")
	@LogAPI("删除对讲群组")
	public TalkbackGroup update(@PathVariable String id, @Validated @RequestBody TalkbackGroup group) {
		assertTalkbackGroupExists(id);

		group.setId(id);

		group = talkbackGroupService.update(group);
		jms.convertAndSend(TopicNameConstants.TALKBACK_GROUP_UPDATE, group);

		return group;
	}

	@GetMapping("/{groupId}/talkback-users")
	public Page<TalkbackUser> talkbackUsers(@PathVariable String groupId, @PageableDefault Pageable pageable) {
		assertTalkbackGroupExists(groupId);

		return talkbackUserService.findByTalkbackGroup(new TalkbackGroup(groupId), pageable);
	}

	@GetMapping("/{id}")
	public TalkbackGroup one(@PathVariable String id) {
		Optional<TalkbackGroup> o = talkbackGroupService.findOne(id);

		return o.orElseThrow(TalkbackGroupNotFoundException::new);
	}

	@RequestMapping
	public Page<TalkbackGroup> list(@PageableDefault Pageable pageable) {
		return talkbackGroupService.findAll(pageable);
	}

}
