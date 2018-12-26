package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(GroupFile.class)
public class GroupFile_ extends TimestampedResource_ {
	public static volatile SingularAttribute<GroupFile, String> id;
	public static volatile SingularAttribute<GroupFile, String> name;
	public static volatile SingularAttribute<GroupFile, String> fileId;
	public static volatile SingularAttribute<GroupFile, String> type;
	public static volatile SingularAttribute<GroupFile, Group> group;
}
