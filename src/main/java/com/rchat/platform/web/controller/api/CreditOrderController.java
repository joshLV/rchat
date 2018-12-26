package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.ServiceException;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class CreditOrderController {
    @Autowired
    private RchatEnv env;
    @Autowired
    private CreditOrderService creditOrderService;
    @Autowired
    private AgentCreditOrderService agentCreditOrderService;
    @Autowired
    private GroupCreditOrderService groupCreditOrderService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AgentService agentService;

    @PatchMapping(path = "/{id}", params = "credit")
    @LogAPI(operation = "下发额度", resource = "额度订单")
    public CreditOrder distribute(@PathVariable String id, @RequestParam Long credit) {
        CreditOrder order = creditOrderService.findOne(id).orElseThrow(CreditOrderNotFoundException::new);

        if (order.getRemanentCredit() < credit) {
            throw new DistributeExcessiveCreditException();
        }

        if (RchatUtils.isAgentAdmin()) {
            Agent agent = env.currentAgent();
            if (!agent.equals(order.getRespondent())) {
                throw new AgentIsNotOrderRespondentException();
            }
        }

        if (order instanceof GroupCreditOrder) {
            GroupCreditOrder gOrder = (GroupCreditOrder) order;
            return groupCreditOrderService.distributeCredit(gOrder, credit);

        } else if (order instanceof AgentCreditOrder) {
            AgentCreditOrder aOrder = (AgentCreditOrder) order;
            return agentCreditOrderService.distributeCredit(aOrder, credit);
        }
        return order;
    }

    @PatchMapping("/direct-distribute/group")
    @LogAPI(operation = "直接下发集团额度", resource = "额度订单")
    public CreditOrder directDistributeGroup(@RequestParam String groupId, @RequestParam Long credit) {
        Group group = groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new);
        return groupCreditOrderService.directDistribute(group, credit);
    }

    @PatchMapping("/direct-distribute/agent")
    @LogAPI(operation = "直接下发代理商额度", resource = "额度订单")
    public CreditOrder directDistributeAgent(@RequestParam String agentId, @RequestParam Long credit) {
        Agent agent = agentService.findOne(agentId).orElseThrow(AgentNotFoundException::new);
        return agentCreditOrderService.directDistribute(agent, credit);
    }

    @PatchMapping("/{orderId}/cancel")
    @LogAPI(operation = "撤销额度申请", resource = "额度订单")
    @ResponseStatus(code = HttpStatus.OK, reason = "撤销额度订单成功")
    public CreditOrder cancelOrder(@PathVariable String orderId) {
        CreditOrder order = creditOrderService.findOne(orderId).orElseThrow(CreditOrderNotFoundException::new);
        CreditOrderStatus status = order.getStatus();
        if (status == CreditOrderStatus.COMPLETED) {
            throw new ServiceException("订单已经完成，无法撤销");
        }

        return creditOrderService.cancel(order);
    }

    @GetMapping("/{id}")
    public CreditOrder one(@PathVariable String id) {
        Optional<CreditOrder> o = creditOrderService.findOne(id);
        return o.orElseThrow(CreditOrderNotFoundException::new);
    }

    @GetMapping
    public Page<CreditOrder> list(@PageableDefault Pageable pageable) {
        return creditOrderService.findAll(pageable);
    }

}
