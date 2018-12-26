package com.rchat.platform.domain;

import java.util.Date;
import java.util.List;

public interface RenewLogRepositoryCustom {
    /**
     * 统计指定额度账户指定你时间区间内，消耗的额度总数
     *
     * @param account 额度账户
     * @param start   开始时间
     * @param end     结束时间
     * @return 额度数量
     */
    long countConsumption(CreditAccount account, Date start, Date end);

    /**
     * 统计指定额度账户今天消耗的额度总数
     *
     * @param account 额度账户
     * @return 额度数量
     */
    long countTodayConsumption(CreditAccount account);

    /**
     * 指定额度账号的消费记录
     *
     * @param account 额度账号
     * @param start   开始时间
     * @param end     结束时间
     * @return 消费记录列表
     */
    List<RenewLog> findConsumption(CreditAccount account, Date start, Date end);
}
