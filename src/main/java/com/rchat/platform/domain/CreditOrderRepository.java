package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditOrderRepository extends JpaRepository<CreditOrder, String>, CreditOrderRepositoryCustom {

    Page<CreditOrder> findByRespondent(Agent agent, Pageable pageable);

    Page<CreditOrder> findByRespondentAndStatusNot(Agent agent, CreditOrderStatus completed, Pageable pageable);

    @Modifying
    @Query("update CreditOrder o set o.status='CANCELED' where o=?1")
    void cancelOrder(CreditOrder order);
}
