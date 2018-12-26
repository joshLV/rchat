package com.rchat.platform.service.impl;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.service.AgentViewService;
import com.rchat.platform.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgentViewServiceImpl extends AbstractService<AgentView, AgentViewId> implements AgentViewService {
    @Autowired
    private AgentViewRepository repository;
    @Autowired
    private SummaryService summaryService;

    @Override
    protected JpaRepository<AgentView, AgentViewId> repository() {
        return repository;
    }

    private Page<Agent> viewPage2AgentPage(Page<AgentView> page, Pageable pageable) {
        Page<Agent> agents = page.map(RchatUtils::view2Entity);
        agents.forEach(agent -> {
            CreditSummary summary = summaryService.countCredit(agent);
            agent.setCreditAccumulation(summary.getCreditAccumulation());
            agent.setCreditRemaint(summary.getCreditRemaint());
        });
        return agents;
    }

    @Override
    public Page<Agent> findChildren(Agent parent, Pageable pageable) {
        Page<AgentView> page = repository.findByViewIdParentId(parent.getId(), pageable);
        return viewPage2AgentPage(page, pageable);
    }

    @Override
    public Page<Agent> search(Agent parent, Optional<String> agentName, Optional<String> adminName,
                              Optional<String> adminUsername, Pageable pageable) {
        Page<AgentView> page = repository.search(parent, agentName, adminName, adminUsername, pageable);
        return viewPage2AgentPage(page, pageable);
    }

    @Override
    public Optional<Agent> findOneUnderAgent(String id, Agent parent) {
        Optional<AgentView> view = repository.findByViewId(new AgentViewId(id, parent.getId()));
        return view.map(RchatUtils::view2Entity);
    }

    @Override
    public List<Agent> findChildren(Agent parent) {
        List<AgentView> views = repository.findByViewIdParentId(parent.getId());
        return views.parallelStream().map(RchatUtils::view2Entity).collect(Collectors.toList());
    }
}
