package com.rchat.platform.service;

import com.rchat.platform.domain.CreditAccount;

public interface CreditAccountService extends GenericService<CreditAccount, String> {

    /**
     * 上缴额度
     *
     * @param parentAccount 增加额度的账号
     * @param account       减少额度的账号
     * @param credit        额度
     */
    void turnOver(CreditAccount parentAccount, CreditAccount account, long credit);
}
