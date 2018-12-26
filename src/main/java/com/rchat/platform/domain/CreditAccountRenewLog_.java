package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CreditAccountRenewLog.class)
public class CreditAccountRenewLog_ extends RenewLog_ {
    public static volatile SingularAttribute<CreditAccountRenewLog, CreditAccount> toCreditAccount;
}
