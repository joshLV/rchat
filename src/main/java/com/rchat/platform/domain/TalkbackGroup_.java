package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TalkbackGroup.class)
public class TalkbackGroup_ extends TimestampedResource_ {
	public static volatile SingularAttribute<TalkbackGroup, String> id;
	public static volatile SingularAttribute<TalkbackGroup, String> name;
	public static volatile SingularAttribute<TalkbackGroup, Group> group;
	public static volatile SingularAttribute<TalkbackGroup, Department> department;
}
