package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.AgentService;
import com.rchat.platform.service.CreditTurnOverOrderService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.web.exception.AgentNotFoundException;
import com.rchat.platform.web.exception.CreditTurnOverOrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-turn-over-orders")
public class CreditTurnOverOrderController {
    @Autowired
    private CreditTurnOverOrderService creditTurnOverOrderService;
    @Autowired
    private RchatEnv env;
    @Autowired
    private AgentService agentService;
    @Autowired
    private GroupService groupService;

    @PostMapping
    @LogAPI(operation = "创建上缴额度订单", resource = "额度账号")
    public CreditTurnOverOrder turnOverOrder(@RequestParam Long credit) {
        CreditTurnOverOrder order;
        Agent respondent;

        if (RchatUtils.isAgentAdmin()) {
            order = new AgentCreditTurnOverOrder(env.currentAgent());
            respondent = agentService.findParent(env.currentAgent()).orElseThrow(AgentNotFoundException::new);
        } else if (RchatUtils.isGroupAdmin()) {
            order = new GroupCreditTurnOverOrder(env.currentGroup());
            respondent = agentService.findByGroup(env.currentGroup()).orElseThrow(AgentNotFoundException::new);
        } else {
            throw new NoRightAccessException();
        }

        order.setRespondent(respondent);
        order.setCredit(credit);
        order.setStatus(TurnOverStatus.NO_ACK);

        return creditTurnOverOrderService.create(order);
    }

    @PatchMapping("/{orderId}")
    @LogAPI(operation = "确认额度上缴", resource = "额度账号")
    public CreditTurnOverOrder ackOrder(@PathVariable String orderId) {
        CreditTurnOverOrder order = creditTurnOverOrderService.findOne(orderId).orElseThrow(CreditTurnOverOrderNotFoundException::new);
        return creditTurnOverOrderService.ackOrder(order);
    }

    @GetMapping
    public Page<CreditTurnOverOrder> findAll(TurnOverStatus status, @PageableDefault Pageable pageable) {
        if (RchatUtils.isAgentAdmin()) {
            if (status == null) {
                return creditTurnOverOrderService.findByAgent(env.currentAgent(), pageable);
            } else {
                return creditTurnOverOrderService.findByAgentAndStatus(env.currentAgent(), status, pageable);
            }
        } else if (RchatUtils.isRchatAdmin()) {
            if (status == null) {
                return creditTurnOverOrderService.findAll(pageable);
            } else {
                return creditTurnOverOrderService.findAllByStatus(status, pageable);
            }
        } else if (RchatUtils.isGroupAdmin()) {
            if (status == null) {
                return creditTurnOverOrderService.findByGroup(env.currentGroup(), pageable);
            } else {
                return creditTurnOverOrderService.findByGroupAndStatus(env.currentGroup(), status, pageable);
            }
        } else {
            throw new NoRightAccessException();
        }
    }
}
