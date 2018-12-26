package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(DepartmentViewId.class)
public class DepartmentViewId_ {
	public static volatile SingularAttribute<DepartmentViewId, String> id;
	public static volatile SingularAttribute<DepartmentViewId, String> parentId;
}
