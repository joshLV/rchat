package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.domain.Group;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.AgentService;
import com.rchat.platform.service.CreditAccountService;
import com.rchat.platform.service.GroupCreditAccountService;
import com.rchat.platform.web.exception.AgentNotFoundException;
import com.rchat.platform.web.exception.CreditAccountNotFoundException;
import com.rchat.platform.web.exception.CreditInsufficiencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/creditAccounts")
public class CreditAccountController {
    @Autowired
    private CreditAccountService creditAccountService;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private GroupCreditAccountService groupCreditAccountService;
    @Autowired
    private RchatEnv env;
    @Autowired
    private AgentService agentService;

    @PatchMapping(params = "credit")
    @LogAPI(operation = "上交额度", resource = "额度账号")
    public void turnOver(@RequestParam Long credit) {
        CreditAccount account;
        Agent agent;
        if (RchatUtils.isAgentAdmin()) {
            agent = env.currentAgent();
            account = agentCreditAccountService.findByAgent(agent);
            agent = agentService.findParent(agent).orElseThrow(AgentNotFoundException::new);
        } else if (RchatUtils.isGroupAdmin()) {
            Group group = env.currentGroup();
            account = groupCreditAccountService.findByGroup(group);
            agent = agentService.findByGroup(group).orElseThrow(AgentNotFoundException::new);
        } else {
            throw new NoRightAccessException();
        }

        if (account.getCredit() < credit) {
            throw new CreditInsufficiencyException();
        }

        CreditAccount parentAccount = agentCreditAccountService.findByAgent(agent);
        creditAccountService.turnOver(parentAccount, account, credit);
    }

    @GetMapping
    public Page<CreditAccount> list(@PageableDefault Pageable pageable) {
        return creditAccountService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CreditAccount one(@PathVariable String id) {
        Optional<CreditAccount> o = creditAccountService.findOne(id);

        return o.orElseThrow(CreditAccountNotFoundException::new);
    }
}
