package com.rchat.platform.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RenewLogRepository extends JpaRepository<RenewLog, String>, RenewLogRepositoryCustom {

}
