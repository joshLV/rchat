package com.rchat.platform.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditAccountRenewLogRepository extends JpaRepository<CreditAccountRenewLog, String>, CreditAccountRenewLogRepositoryCustom {

}
