package com.rchat.platform.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCreditAccountRepository extends JpaRepository<GroupCreditAccount, String> {
	CreditAccount findByGroup(Group group);

	void deleteByGroup(Group group);
}
