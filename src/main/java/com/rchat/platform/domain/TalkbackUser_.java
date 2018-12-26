package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@StaticMetamodel(TalkbackUser.class)
public class TalkbackUser_ extends TimestampedResource_ {
    public static volatile SingularAttribute<TalkbackUser, String> id;
    public static volatile SingularAttribute<TalkbackUser, TalkbackNumber> number;
    public static volatile SingularAttribute<TalkbackUser, Group> group;
    public static volatile SingularAttribute<TalkbackUser, Department> department;
    public static volatile SingularAttribute<TalkbackUser, Date> lastRenewed;
    public static volatile SingularAttribute<TalkbackUser, Date> deadline;
    public static volatile SingularAttribute<TalkbackUser, Boolean> activated;
    public static volatile SingularAttribute<TalkbackUser, Date> activatedAt;
    public static volatile SingularAttribute<TalkbackUser, Boolean> enabled;
    public static volatile SingularAttribute<TalkbackUser, String> password;
    public static volatile SingularAttribute<TalkbackUser, String> name;
    public static volatile SingularAttribute<TalkbackUser, TalkbackRole> role;
}
