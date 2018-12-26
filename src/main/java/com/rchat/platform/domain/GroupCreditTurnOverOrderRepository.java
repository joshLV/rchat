package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCreditTurnOverOrderRepository extends JpaRepository<GroupCreditTurnOverOrder, String> {
    Page<CreditTurnOverOrder> findByGroup(Group group, Pageable pageable);

    Page<CreditTurnOverOrder> findByGroupAndStatus(Group group, TurnOverStatus status, Pageable pageable);
}
