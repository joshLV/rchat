package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.*;
import com.rchat.platform.web.controller.api.view.RenewLogView;
import com.rchat.platform.web.controller.api.view.SummaryView;
import com.rchat.platform.web.controller.api.view.TalkbackUserStatView;
import com.rchat.platform.web.exception.AgentNotFoundException;
import com.rchat.platform.web.exception.DepartmentNotFoundException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private AgentService agentService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private RenewLogService renewLogService;
    @Autowired
    private TalkbackUserRenewLogService talkbackUserRenewLogService;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private SummaryService summaryService;
    @Autowired
    private RchatEnv env;

    @GetMapping("/summary/agents/{agentId}")
    public SummaryView agentSummary(@PathVariable String agentId) {
        Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);

        Summary summary = summaryService.count(agent);

        SummaryView view = new SummaryView();
        view.setId(agent.getId());
        view.setName(agent.getName());
        view.setLevel(agent.getLevel());
        view.setRegion(agent.getRegion());
        view.setUserAmount(summary.getUserAmount());
        view.setExpiredUserAmount(summary.getExpiredUserAmount());
        view.setNonactiveUserAmount(summary.getNonactiveUserAmount());
        view.setCreditAccumulation(summary.getCreditAccumulation());
        view.setCreditRemaint(summary.getCreditRemaint());
        view.setEpxiringUserAmount(summary.getExpiringUserAmount());
        return view;
    }


    @GetMapping("/summary/groups/{groupId}")
    public SummaryView groupSummary(@PathVariable String groupId) {
        Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);

        Summary summary = summaryService.count(group);

        SummaryView view = new SummaryView();
        view.setId(group.getId());
        view.setName(group.getName());
        view.setUserAmount(summary.getUserAmount());
        view.setExpiredUserAmount(summary.getExpiredUserAmount());
        view.setNonactiveUserAmount(summary.getNonactiveUserAmount());
        view.setCreditAccumulation(summary.getCreditAccumulation());
        view.setCreditRemaint(summary.getCreditRemaint());

        return view;
    }

    @GetMapping("/credit")
    public CreditSummary creditSummary(@RequestParam(required = false) String agentId,
                                       @RequestParam(required = false) String groupId) {
        if (RchatUtils.isRchatAdmin()) {
            if (agentId != null) {
                Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                return summaryService.countCredit(agent);
            } else if (groupId != null) {
                Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                return summaryService.countCredit(group);
            } else {
                return summaryService.countCredit(env.currentAgent());
            }
        } else if (RchatUtils.isAgentAdmin()) {
            return summaryService.countCredit(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            return summaryService.countCredit(env.currentGroup());
        } else {
            throw new NoRightAccessException();
        }


    }


    @GetMapping("/summary")
    public Summary summary(@RequestParam(required = false) String agentId,
                           @RequestParam(required = false) String groupId,
                           @RequestParam(required = false) String departmentId) {
        if (RchatUtils.isRchatAdmin()) {
            if (Optional.ofNullable(agentId).isPresent()) {
                return summaryService.count(agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new));
            }

            if (Optional.ofNullable(groupId).isPresent()) {
                return summaryService.count(groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new));
            }

            if (Optional.ofNullable(departmentId).isPresent()) {
                return summaryService.count(departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new));
            }
        }
        return summaryService.count();
    }

    /**
     * 过期了的用户
     */
    @GetMapping("/expired-talkback-users")
    public Page<TalkbackUserStatView> expiredTalkbackUserViews(@RequestParam(required = false) String groupName,
                                                               @RequestParam(required = false) String agentName,
                                                               @RequestParam(required = false) String fullValue,
                                                               @PageableDefault Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            Page<TalkbackUser> users = talkbackUserService.statExpiredTalkbackUsers(groupName, agentName, fullValue, pageable);
            return users.map(this::convert2View);
        } else if (RchatUtils.isAgentAdmin() || RchatUtils.isGroupAdmin()) {
            Page<TalkbackUser> users = talkbackUserService.statExpiredTalkbackUsers(fullValue, pageable);
            return users.map(this::convert2View);
        } else {
            throw new NoRightAccessException();
        }
    }

    /**
     * 即将过期的用户
     */
    @GetMapping("/expiring-talkback-users")
    public Page<TalkbackUserStatView> expiringTalkbackUserViews(@RequestParam(required = false) String groupName,
                                                                @RequestParam(required = false) String agentName,
                                                                @RequestParam(required = false) String fullValue,
                                                                @PageableDefault Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            Page<TalkbackUser> users = talkbackUserService.statWarningTalkbackUsers(groupName, agentName, fullValue, pageable);
            return users.map(this::convert2View);
        } else if (RchatUtils.isAgentAdmin() || RchatUtils.isGroupAdmin()) {
            Page<TalkbackUser> users = talkbackUserService.statWarningTalkbackUsers(fullValue, pageable);
            return users.map(this::convert2View);
        } else {
            throw new NoRightAccessException();
        }
    }

    /**
     * 已激活的的用户
     */
    @GetMapping("/activated-talkback-users")
    public Page<TalkbackUserStatView> activatedTalkbackUserViews(@RequestParam(required = false) String groupName,
                                                                 @RequestParam(required = false) String agentName,
                                                                 @RequestParam(required = false) String fullValue,
                                                                 @PageableDefault Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            Page<TalkbackUser> users = talkbackUserService.statActivatedTalkbackUsers(groupName, agentName, fullValue, pageable);
            return users.map(this::convert2View);
        } else if (RchatUtils.isAgentAdmin() || RchatUtils.isGroupAdmin()) {
            Page<TalkbackUser> users = talkbackUserService.statActivatedTalkbackUsers(fullValue, pageable);
            return users.map(this::convert2View);
        } else {
            throw new NoRightAccessException();
        }
    }

    /**
     * 续费日志
     *
     * @param pageable 分页信息
     * @return 续费日志分页数据
     */
    @GetMapping("/talkback-user-renew-logs")
    public Page<RenewLogView> renewLogs(@RequestParam(required = false) String agentId,
                                        @RequestParam(required = false) String groupId,
                                        @RequestParam(required = false) Date start,
                                        @RequestParam(required = false) Date end,
                                        @RequestParam(required = false) String fullValue,
                                        @PageableDefault Pageable pageable) {

        Page<TalkbackUserRenewLog> logs = talkbackUserRenewLogService.statRenewLogs(agentId, groupId, start, end, fullValue, pageable);
        return logs.map(this::convert2view);
    }

    private TalkbackUserStatView convert2View(TalkbackUser user) {
        TalkbackUserStatView statView = new TalkbackUserStatView();

        statView.setId(user.getId());
        statView.setFullValue(user.getNumber().getFullValue());
        statView.setActivatedAt(user.getActivatedAt());
        statView.setAgent(user.getGroup().getAgent().getName());
        statView.setGroup(user.getGroup().getName());
        statView.setName(user.getName());
        statView.setDeadline(user.getDeadline());
        return statView;
    }

    private RenewLogView convert2view(TalkbackUserRenewLog log) {
        RenewLogView view = new RenewLogView();
        view.setId(log.getId());
        view.setActivatedAt(log.getActivatedAt());
        view.setGroup(log.getGroup().getName());
        view.setAgent(log.getAgent().getName());

        view.setRenewAt(log.getCreatedAt());
        view.setNumber(log.getNumber());
        view.setDeadline(log.getDeadline());
        view.setBasicCredit(log.getBasicCredit());
        return view;
    }
}

