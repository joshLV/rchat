package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TalkbackUserRenewLog.class)
public class TalkbackUserRenewLog_ extends RenewLog_ {
    public static volatile SingularAttribute<RenewLog, TalkbackUser> user;
}
