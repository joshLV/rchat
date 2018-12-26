package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RenewLog.class)
public class RenewLog_ extends TimestampedResource_ {
    public static volatile SingularAttribute<RenewLog, String> id;
    public static volatile SingularAttribute<RenewLog, CreditAccount> creditAccount;
    public static volatile SingularAttribute<RenewLog, Long> credit;
}
