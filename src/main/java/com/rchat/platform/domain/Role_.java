package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Role.class)
public class Role_ {
	public static volatile SingularAttribute<Role, String> id;
	public static volatile SingularAttribute<Role, String> name;
}
