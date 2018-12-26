package com.rchat.platform.web.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.Segment;
import com.rchat.platform.domain.UnassignedSegment;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.AgentSegmentService;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.service.SegmentService;
import com.rchat.platform.service.UnassignedSegmentService;
import com.rchat.platform.web.controller.Status;
import com.rchat.platform.web.exception.SegmentExistsException;
import com.rchat.platform.web.exception.SegmentNotFoundException;

@RestController
@RequestMapping("/api/segments")
public class SegmentController {
	@Autowired
	private SegmentService segmentService;
	@Autowired
	private AgentSegmentService agentSegmentService;
	@Autowired
	private GroupSegmentService groupSegmentService;
	@Autowired
	private UnassignedSegmentService unassignedSegmentService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@LogAPI("创建号段")
	public Segment create(@RequestBody UnassignedSegment segment) {
		if (RchatUtils.isRchatAdmin()) {
			if (segmentService.existsAgentSegment(segment.getValue()))
				throw new SegmentExistsException();
			return segmentService.create(segment);
		} else {
			throw new NoRightAccessException();
		}
	}

	@PostMapping("/agent-segments")
	@ResponseStatus(HttpStatus.CREATED)
	@LogAPI("创建代理商号段")
	public Segment createAgentSegment(@RequestBody AgentSegment segment) {
		if (RchatUtils.isRchatAdmin()) {
			if (agentSegmentService.existsByValue(segment.getValue()))
				throw new SegmentExistsException();
			return agentSegmentService.create(segment);
		} else {
			throw new NoRightAccessException();
		}
	}

	@PostMapping("/group-segments")
	@ResponseStatus(HttpStatus.CREATED)
	@LogAPI("创建集团号段")
	public Segment createGroupSegment(@RequestBody GroupSegment segment) {
		if (RchatUtils.isRchatAdmin()) {
			if (groupSegmentService.existsByValue(segment.getValue()))
				throw new SegmentExistsException();
			return groupSegmentService.create(segment);
		} else {
			throw new NoRightAccessException();
		}
	}

	@DeleteMapping("/{id}")
	@LogAPI("删除号段")
	public Status delete(@PathVariable String id) {
		assertSegmentExists(id);

		if (RchatUtils.isRchatAdmin()) {
			segmentService.delete(id);

			return new Status(HttpStatus.OK, "删除号段成功");
		} else {
			throw new NoRightAccessException();
		}
	}

	@PatchMapping("/{id}")
	@LogAPI("回收号段")
	public Segment recycleSegment(@PathVariable String id) {
		assertSegmentExists(id);

		return segmentService.recycle(new UnassignedSegment(id));
	}

	private void assertSegmentExists(String id) {
		boolean exists = segmentService.exists(id);
		if (!exists)
			throw new SegmentNotFoundException();
	}

	/**
	 * 将号段分配给集团用户
	 * 
	 * @param id
	 * @param groupId
	 * @return
	 */
	@PatchMapping(path = "/{id}", params = "groupId")
	@LogAPI("分配集团号段")
	public Segment grantGroup(@PathVariable String id, @RequestParam String groupId) {
		Optional<Segment> o = segmentService.findOne(id);
		Segment segment = o.orElseThrow(SegmentNotFoundException::new);

		return segmentService.grantGroup(segment, new Group(groupId));
	}

	/**
	 * 将号段分配给代理商
	 * 
	 * @param id
	 * @param agentId
	 * @return
	 */
	@PatchMapping(path = "/{id}", params = "agentId")
	@LogAPI("分配代理商号段")
	public Segment grantAgent(@PathVariable String id, @RequestParam String agentId) {
		Optional<Segment> o = segmentService.findOne(id);
		Segment segment = o.orElseThrow(SegmentNotFoundException::new);

		return segmentService.grantAgent(segment, new Agent(agentId));
	}

	@GetMapping("/{id}")
	public Segment one(@PathVariable String id) {
		Optional<Segment> o = segmentService.findOne(id);

		return o.orElseThrow(SegmentNotFoundException::new);
	}

	@GetMapping
	public Page<Segment> list(@PageableDefault Pageable pageable) {
		return segmentService.findAll(pageable);
	}

	/**
	 * 根据owner类型查询号段
	 * 
	 * @param ownerType
	 *            号段所有者类型
	 * @return
	 */
	@GetMapping(params = "owner-type")
	public Page<? extends Segment> list(@RequestParam(name = "owner-type") String ownerType, Optional<Boolean> internal,
			@PageableDefault Pageable pageable) {
		if (internal.isPresent()) {
			boolean b = internal.get().booleanValue();
			switch (ownerType) {
			case "group":
				return groupSegmentService.findByInternal(b, pageable);
			case "agent":
				return agentSegmentService.findByInternal(b, pageable);
			case "unassigned":
				return unassignedSegmentService.findAll(pageable);
			default:
				return segmentService.findAll(pageable);
			}
		} else {
			switch (ownerType) {
			case "group":
				return groupSegmentService.findAll(pageable);
			case "agent":
				return agentSegmentService.findAll(pageable);
			case "unassigned":
				return unassignedSegmentService.findAll(pageable);
			default:
				return segmentService.findAll(pageable);
			}
		}
	}
}
