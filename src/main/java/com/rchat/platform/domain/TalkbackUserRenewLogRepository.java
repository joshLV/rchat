package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TalkbackUserRenewLogRepository extends JpaRepository<TalkbackUserRenewLog, String>, TalkbackUserRenewLogRepositoryCustom {
    Page<TalkbackUserRenewLog> findByUser(TalkbackUser user, Pageable pageable);

    Page<TalkbackUserRenewLog> findByUserGroup(Group group, Pageable pageable);

    Page<TalkbackUserRenewLog> findByCreatedAtBetween(Date start, Date end, Pageable pageable);

    Page<TalkbackUserRenewLog> findByUserGroupAgent(Agent agent, Pageable pageable);
}
