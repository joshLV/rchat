package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditTurnOverOrderRepository extends JpaRepository<CreditTurnOverOrder, String> {

    @Modifying
    @Query("update CreditTurnOverOrder o set o.status=1 where o=?1")
    void ackOrder(CreditTurnOverOrder order);

    Page<CreditTurnOverOrder> findByRespondent(Agent agent, Pageable pageable);

    Page<CreditTurnOverOrder> findByStatus(TurnOverStatus status, Pageable pageable);

    Page<CreditTurnOverOrder> findByRespondentAndStatus(Agent agent, TurnOverStatus status, Pageable pageable);
}
