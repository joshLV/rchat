package com.rchat.platform.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentCreditAccountRepository extends JpaRepository<AgentCreditAccount, String> {
	CreditAccount findByAgent(Agent agent);

	void deleteByAgent(Agent agent);
}
