package com.rchat.platform.domain;

public interface GroupSummaryRepository {
    /**
     * 指定代理商直属集团总数
     *
     * @param agent 代理商
     * @return 集团数量
     */
    long countTotalAmount(Agent agent);

    /**
     * 新增集团总数
     *
     * @param agent 指定代理商
     * @return 集新增集团数量
     */
    long countNewAmount(Agent agent);
}
