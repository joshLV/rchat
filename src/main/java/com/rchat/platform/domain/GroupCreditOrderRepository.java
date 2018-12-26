package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCreditOrderRepository extends JpaRepository<GroupCreditOrder, String> {
	Page<GroupCreditOrder> findByGroup(Group group, Pageable pageable);

	void deleteByGroup(Group group);
}
