package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Group.class)
public class Group_ extends TimestampedResource_ {
	public static volatile SingularAttribute<Group, String> id;
	public static volatile SingularAttribute<Group, String> name;
	public static volatile SingularAttribute<Group, Boolean> status;
	public static volatile SingularAttribute<Group, Server> server;
	public static volatile SingularAttribute<Group, Agent> agent;
	public static volatile SingularAttribute<Group, User> administrator;
}
