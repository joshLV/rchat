package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TalkbackUserRepositoryCustom extends TalkbackUserSummaryRepository {

    /**
     * 批量回收对讲账号
     *
     * @param users 对讲账号列表
     * @return 回收成功，返回<code>true</code>
     */
    boolean recycle(List<TalkbackUser> users);

    /**
     * 批量使能禁用对讲账号
     *
     * @param users   对讲账号列表
     * @param enabled 是否使能
     * @return 使能或禁用成功，返回<code>true</code>
     */
    boolean setEnabled(List<TalkbackUser> users, boolean enabled);

    Page<TalkbackUser> search(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated,
    						  Optional<Department> department, Optional<Group> group, Optional<Agent> agent, Optional<Date> createdStart, Optional<Date> createdEnd,
                              Optional<Date> renewStart, Optional<Date> renewEnd, Pageable pageable);

	Page<TalkbackUser> page(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue,
			Optional<String> name, Optional<Boolean> activated, Optional<String> departmentId, Optional<String> groupId,
			Optional<String> agentId, Optional<Date> createdStart, Optional<Date> createdEnd, Optional<Date> renewStart,
			Optional<Date> renewEnd, Pageable pageable);

    Page<Brief> findBriefs(Pageable pageable);

    Page<TalkbackUser> statExpiredTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable);

    Page<TalkbackUser> statExpiredTalkbackUsers(Agent agent, Group group, String fullValue, Pageable pageable);

    Page<TalkbackUser> statExpiringTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable);

    Page<TalkbackUser> statExpiringTalkbackUsers(Agent agent, Group group, String fullValue, Pageable pageable);
    
    Page<TalkbackUser> statActivatedTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable);

    Page<TalkbackUser> statActivatedTalkbackUsers(Agent agent, Group group, String fullValue, Pageable pageable);

    Page<Brief> findBriefs(Server server, Pageable pageable);
}
