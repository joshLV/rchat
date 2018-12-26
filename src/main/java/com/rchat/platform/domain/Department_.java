package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Department.class)
public class Department_ extends TimestampedResource_ {
	public static volatile SingularAttribute<Department, String> id;
	public static volatile SingularAttribute<Department, String> name;
	public static volatile SingularAttribute<Department, User> administrator;
	public static volatile SingularAttribute<Department, Group> group;
}
