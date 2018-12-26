package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.AgentNotFoundException;
import com.rchat.platform.web.exception.DepartmentNotFoundException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.view.AgentXlsxView;
import com.rchat.platform.web.view.CreditXlsxView;
import com.rchat.platform.web.view.TalkbackUserRenewLogXlsxView;
import com.rchat.platform.web.view.TalkbackUserXlsxView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {
    @Autowired
    private TalkbackUserService talkbackUserService;
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

    @GetMapping("/credit.xlsx")
    public ModelAndView exportCredit(@RequestParam(required = false) String agentId,
                                     @RequestParam(required = false) String groupId, Integer type) {
        CreditXlsxView view = new CreditXlsxView();
        String filename;
        List<? extends RenewLog> logs;
        switch (type) {
            case 0:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findAccumulation(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findAccumulation(group);
                } else {
                    logs = renewLogService.findAccumulation();
                }
                filename = "累计购买额度";
                break;
            case 1:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findYearAccumulation(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findYearAccumulation(group);
                } else {
                    logs = renewLogService.findYearAccumulation();
                }
                filename = "本年购买额度";
                break;
            case 2:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findSeasonAccumulation(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findSeasonAccumulation(group);
                } else {
                    logs = renewLogService.findSeasonAccumulation();
                }
                filename = "本季度购买额度";
                break;
            case 3:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findMonthAccumulation(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findMonthAccumulation(group);
                } else {
                    logs = renewLogService.findMonthAccumulation();
                }

                filename = "本月购买额度";
                break;
            case 4:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findConsumption(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findConsumption(group);
                } else {
                    logs = renewLogService.findConsumption();
                }
                filename = "累计销售额度";
                break;
            case 5:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findYearConsumption(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findYearConsumption(group);
                } else {
                    logs = renewLogService.findYearConsumption();
                }
                filename = "本年销售额度";
                break;
            case 6:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findSeasonConsumption(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findSeasonConsumption(group);
                } else {
                    logs = renewLogService.findSeasonConsumption();
                }
                filename = "本季度销售额度";
                break;
            case 7:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    logs = renewLogService.findMonthConsumption(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    logs = renewLogService.findMonthConsumption(group);
                } else {
                    logs = renewLogService.findMonthConsumption();
                }
                filename = "本月销售额度";
                break;
            default:
                logs = Collections.emptyList();
                filename = "额度记录";
                break;
        }
        view.setFilename(filename);
        view.setRenewLogs(logs);

        return new ModelAndView(view);
    }

    @GetMapping("/talkback-user-renew-logs.xlsx")
    public View exportRenewLog(String agentId, String groupId, Date start, Date end, String fullValue) {
        List<TalkbackUserRenewLog> logs = talkbackUserRenewLogService.findRenewLogs(agentId, groupId, start, end, fullValue);
        TalkbackUserRenewLogXlsxView view = new TalkbackUserRenewLogXlsxView();
        view.setRenewLogs(logs);
        view.setFilename("续费日志");
        return view;
    }


    @GetMapping("/agents.xlsx")
    public ModelAndView exportAgent(Integer level) {
        if (RchatUtils.isRchatAdmin()) {
            List<Agent> agents = agentService.findByLevel(level);
            AgentXlsxView view = new AgentXlsxView();
            view.setAgents(agents);
            String filename;
            switch (level) {
                case 1:
                    filename = "一级代理商";
                    break;
                case 2:
                    filename = "二级代理商";
                    break;
                case 3:
                    filename = "三级代理商";
                    break;
                case 4:
                    filename = "四级代理商";
                    break;
                case 5:
                    filename = "五级代理商";
                    break;
                default:
                    filename = "代理商";
                    break;
            }
            view.setFilename(filename);

            return new ModelAndView(view);
        } else {
            throw new NoRightAccessException();
        }
    }

    @GetMapping("/talkback-users.xlsx")
    public ModelAndView exportTalkbackUser(@RequestParam(required = false) String agentId,
                                           @RequestParam(required = false) String groupId,
                                           @RequestParam(required = false) String departmentId,
                                           Integer type) {
        TalkbackUserXlsxView view = new TalkbackUserXlsxView();

        List<TalkbackUser> users;
        String filename;
        switch (type) {
            case 0:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findAll(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findAll(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findAll(department);
                } else {
                    users = talkbackUserService.findAll();
                }
                filename = "全部对讲账号";
                break;
            case 1:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findExpiring(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findExpiring(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findExpiring(department);
                } else {
                    users = talkbackUserService.findExpiring();
                }
                filename = "预警账号";
                break;
            case 2:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findExpired(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findExpired(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findExpired(department);
                } else {
                    users = talkbackUserService.findExpired();
                }
                filename = "过期账号";
                break;
            case 3:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findNew(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findNew(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findNew(department);
                } else {
                    users = talkbackUserService.findNew();
                }
                filename = "当日新增账号";
                break;
            case 4:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findNewActivated(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findNewActivated(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findNewActivated(department);
                } else {
                    users = talkbackUserService.findNewActivated();
                }
                filename = "当日激活账号";
                break;
            case 5:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findNewExpired(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findNewExpired(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findNewExpired(department);
                } else {
                    users = talkbackUserService.findNewExpired();
                }
                filename = "当日过期账号";
                break;
            case 6:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findNonactivated(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findNonactivated(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findNonactivated(department);
                } else {
                    users = talkbackUserService.findNonactivated();
                }
                filename = "未激活账号";
                break;
            case 7:
                if (agentId != null) {
                    Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
                    users = talkbackUserService.findActivated(agent);
                } else if (groupId != null) {
                    Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
                    users = talkbackUserService.findActivated(group);
                } else if (departmentId != null) {
                    Department department = departmentService.findOne(departmentId).orElseThrow(DepartmentNotFoundException::new);
                    users = talkbackUserService.findActivated(department);
                } else {
                    users = talkbackUserService.findActivated();
                }
                filename = "已激活账号";
                break;
            default:
                users = Collections.emptyList();
                filename = "对讲账号";
                break;
        }
        view.setFilename(filename);
        view.setUsers(users);

        return new ModelAndView(view);
    }
}
