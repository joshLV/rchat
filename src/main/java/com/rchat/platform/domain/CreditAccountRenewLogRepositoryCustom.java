package com.rchat.platform.domain;

import java.util.Date;
import java.util.List;

public interface CreditAccountRenewLogRepositoryCustom {
    /**
     * 统计额度账户，指定时间区间累计充值的额度数量
     *
     * @param account 额度账户
     * @param start   开始时间
     * @param end     结束时间
     * @return 额度数量
     */
    long countAccumulation(CreditAccount account, Date start, Date end);

    /**
     * 充值额度记录
     *
     * @param account 入账额度账号
     * @param start   开始时间
     * @param end     结束时间
     * @return 下发额度记录列表
     */
    List<CreditAccountRenewLog> findAccumulation(CreditAccount account, Date start, Date end);
}
