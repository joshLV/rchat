package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.TalkbackNumberService;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.exception.SegmentExistsException;
import com.rchat.platform.web.exception.SegmentNotFoundException;
import com.rchat.platform.web.exception.TalkbackNumberNotEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/group-segments")
public class GroupSegmentController {
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private TalkbackNumberService talkbackNumberService;
    @Autowired
    private GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupSegment create(@Validated @RequestBody GroupSegment segment) {
        if (RchatUtils.isRchatAdmin()) {
            if (groupSegmentService.existsByAgentSegmentAndValue(segment.getAgentSegment(), segment.getValue()))
                throw new SegmentExistsException();
            return groupSegmentService.create(segment);
        } else {
            throw new NoRightAccessException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除集团号段成功")
    @LogAPI("删除集团号段")
    public void delete(@PathVariable String id) {
        if (talkbackNumberService.existsByGroupSegment(new GroupSegment(id))) {
            throw new TalkbackNumberNotEmptyException();
        }
        groupSegmentService.delete(id);
    }

    @PatchMapping("/{id}")
    @LogAPI("分配集团商号段")
    public void distribute(@PathVariable String id, @RequestParam String groupId) {
        // 判断号段是否存在
        GroupSegment segment = getGroupSegment(id);
        Group group = getGroup(groupId);
        segment.setGroup(group);

        groupSegmentService.update(segment);
    }

    private Group getGroup(String groupId) {
        return groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
    }

    private GroupSegment getGroupSegment(String id) {
        return groupSegmentService.findOne(id).orElseThrow(SegmentNotFoundException::new);
    }

    @PutMapping("/{id}")
    @LogAPI("更新集团号段")
    public GroupSegment update(@PathVariable String id, @Validated @RequestBody GroupSegment segment) {
        if (talkbackNumberService.existsByGroupSegment(segment)) {
            throw new TalkbackNumberNotEmptyException();
        }
        GroupSegment existsSegment = groupSegmentService.findOne(id).orElseThrow(SegmentNotFoundException::new);
        if (existsSegment.getValue() != segment.getValue() && groupSegmentService.existsByAgentSegmentAndValue(segment.getAgentSegment(), segment.getValue())) {
            throw new SegmentExistsException();
        }
        return groupSegmentService.update(segment);
    }

    @GetMapping("/{id}")
    public GroupSegment detail(@PathVariable String id) {
        return groupSegmentService.findOne(id).orElseThrow(SegmentNotFoundException::new);
    }

    @GetMapping
    public Page<GroupSegment> list(Optional<Boolean> internal, @PageableDefault Pageable pageable) {
        if (internal.isPresent())
            return groupSegmentService.findByInternal(internal.get(), pageable);
        else
            return groupSegmentService.findAll(pageable);
    }

}
