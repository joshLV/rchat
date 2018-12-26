package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface TalkbackUserRenewLogRepositoryCustom {
    /**
     * 统计对讲账号续费日志，除了pageable，其他参数均可以为空值，当为空值的时候，不作为查询条件
     *
     * @param agent     代理商
     * @param group     集团
     * @param start     开始时间
     * @param end       结束时间
     * @param fullValue 对讲账号
     * @param pageable  分页信息
     * @return 续费日志分页
     */
    Page<TalkbackUserRenewLog> statRenewLogs(Agent agent, Group group, Date start, Date end, String fullValue, Pageable pageable);

    List<TalkbackUserRenewLog> findRenewLogs(Agent agent, Group group, Date start, Date end, String fullValue);

}
