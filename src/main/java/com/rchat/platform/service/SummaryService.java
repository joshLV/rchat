package com.rchat.platform.service;

import com.rchat.platform.domain.*;

/**
 * 综述统计服务
 */
public interface SummaryService {
    /**
     * 获取当前用户的统计信息
     *
     * @return 统计
     */
    Summary count();

    Summary count(Agent agent);

    Summary count(Group group);

    CreditSummary countCredit(Agent agent);

    CreditSummary countCredit(Group group);

    Summary count(Department department);
}
