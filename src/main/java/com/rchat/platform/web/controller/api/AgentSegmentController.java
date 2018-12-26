package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentSegment;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.AgentSegmentService;
import com.rchat.platform.service.AgentService;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.web.exception.AgentNotFoundException;
import com.rchat.platform.web.exception.GroupSegmentNotEmptyException;
import com.rchat.platform.web.exception.SegmentExistsException;
import com.rchat.platform.web.exception.SegmentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/agent-segments")
public class AgentSegmentController {
    @Autowired
    private AgentSegmentService agentSegmentService;
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private AgentService agentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgentSegment create(@Validated @RequestBody AgentSegment segment) {
        if (RchatUtils.isRchatAdmin()) {
            if (agentSegmentService.existsByValue(segment.getValue()))
                throw new SegmentExistsException();
            return agentSegmentService.create(segment);
        } else {
            throw new NoRightAccessException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除代理商号段成功")
    @LogAPI("删除代理商号段")
    public void delete(@PathVariable String id) {
        if (groupSegmentService.existsByAgentSegment(new AgentSegment(id)))
            throw new GroupSegmentNotEmptyException();

        agentSegmentService.delete(id);
    }

    @PatchMapping("/{id}")
    @LogAPI("分配代理商号段")
    public void distribute(@PathVariable String id, @RequestParam String agentId) {
        // 判断号段是否存在
        AgentSegment segment = getAgentSegment(id);
        Agent agent = getAgent(agentId);
        segment.setAgent(agent);

        agentSegmentService.update(segment);
    }

    private Agent getAgent(String agentId) {
        return agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
    }

    private AgentSegment getAgentSegment(String id) {
        return agentSegmentService.findOne(id).orElseThrow(SegmentNotFoundException::new);
    }

    @PutMapping("/{id}")
    @LogAPI("更新代理商号段")
    public AgentSegment update(@PathVariable String id, @Validated @RequestBody AgentSegment segment) {
        // 如果此代理商好段存在下属集团号段的话，是不能重新分配的
        if (groupSegmentService.existsByAgentSegment(segment))
            throw new GroupSegmentNotEmptyException();

        if (agentSegmentService.existsByValue(segment.getValue()))
            throw new SegmentExistsException();

        return agentSegmentService.update(segment);
    }

    @GetMapping("/{id}")
    public AgentSegment detail(@PathVariable String id) {
        return agentSegmentService.findOne(id).orElseThrow(SegmentNotFoundException::new);
    }

    @GetMapping
    public Page<AgentSegment> list(Optional<Boolean> internal, @PageableDefault Pageable pageable) {
        if (internal.isPresent()) {
            return agentSegmentService.findByInternal(internal.get(), pageable);
        } else {
            return agentSegmentService.findAll(pageable);
        }
    }

    @GetMapping("/{id}/group-segments")
    public Page<GroupSegment> groupSegments(@PathVariable String id, @RequestParam Optional<Boolean> internal,
                                            @PageableDefault Pageable pageable) {
        AgentSegment agentSegment = agentSegmentService.findOne(id).orElseThrow(SegmentNotFoundException::new);
        if (internal.isPresent()) {
            return groupSegmentService.findByAgentSegment(agentSegment, internal.get(), pageable);
        } else {
            return groupSegmentService.findByAgentSegment(agentSegment, pageable);
        }
    }
}
