package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(DepartmentView.class)
public class DepartmentView_ {
	public static volatile SingularAttribute<DepartmentView, DepartmentViewId> viewId;
	public static volatile SingularAttribute<DepartmentView, String> name;
	public static volatile SingularAttribute<DepartmentView, User> administrator;
	public static volatile SingularAttribute<DepartmentView, Group> group;
}
