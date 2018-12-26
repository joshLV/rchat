package com.rchat.platform.domain;

import java.util.List;

public interface TalkbackUserSummaryRepository {
    /**
     * 对讲账号总数
     */
    long countTotalAmount(Agent agent, Group group, Department department);

    /**
     * 过期账号数
     */
    long countExpiredAmount(Agent agent, Group group, Department department);

    /**
     * 新账号数
     */
    long countNewAmount(Agent agent, Group group, Department department);

    /**
     * 未激活账号
     */
    long countNonactivatedAmount(Agent agent, Group group, Department department);

    /**
     * 今天激活账号数
     */
    long countNewActivatedAmount(Agent agent, Group group, Department department);

    /**
     * 即将过期账号数
     */
    long countExpiringAmount(Agent agent, Group group, Department department);

    /**
     * 今天要过期的账号数
     */
    long countNewExpiredAmount(Agent agent, Group group, Department department);

    /**
     * 即将过期账号
     */
    List<TalkbackUser> findExpiring(Agent agent, Group group, Department department);

    /**
     * 过期账号
     */
    List<TalkbackUser> findExpired(Agent agent, Group group, Department department);

    /**
     * 新增账号
     */
    List<TalkbackUser> findNew(Agent agent, Group group, Department department);

    /**
     * 未激活账号
     */
    List<TalkbackUser> findNonactivated(Agent agent, Group group, Department department);
    
    /**
     * 未激活账号
     */
    List<TalkbackUser> findActivated(Agent agent, Group group, Department department);

    /**
     * 当日激活账号
     */
    List<TalkbackUser> findNewActivated(Agent agent, Group group, Department department);

    /**
     * 当日过期账号
     */
    List<TalkbackUser> findNewExpired(Agent agent, Group group, Department department);

    /**
     * 总数
     */
    List<TalkbackUser> findAll(Agent agent, Group group, Department department);
}
