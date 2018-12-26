package com.rchat.platform.service;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.domain.TalkbackUserRenewLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface TalkbackUserRenewLogService extends GenericService<TalkbackUserRenewLog, String> {
    Page<TalkbackUserRenewLog> findByUser(TalkbackUser user, Pageable pageable);

    Page<TalkbackUserRenewLog> findByUserGroup(Group group, Pageable pageable);

    Page<TalkbackUserRenewLog> findByCreatedAtBetween(Date start, Date end, Pageable pageable);

    Page<TalkbackUserRenewLog> findByUserGroupAgent(Agent agent, Pageable pageable);

    Page<TalkbackUserRenewLog> statRenewLogs(String agentId, String groupId, Date start, Date end, String fullValue, Pageable pageable);

    @SecurityMethod(operation = OperationType.RETRIEVE)
    List<TalkbackUserRenewLog> findRenewLogs(String agentId, String groupId, Date start, Date end, String fullValue);
}
