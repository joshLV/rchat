package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentCreditTurnOverOrderRepository extends JpaRepository<AgentCreditTurnOverOrder, String> {
    Page<CreditTurnOverOrder> findByAgentOrRespondentAndStatus(Agent agent, Agent respondent, TurnOverStatus status, Pageable pageable);

    Page<CreditTurnOverOrder> findByAgentOrRespondent(Agent agent, Agent respondent, Pageable pageable);
}
