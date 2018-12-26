package com.rchat.platform.domain;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(BusinessRent.class)
public class BusinessRent_ {
	public static volatile SingularAttribute<BusinessRent, String> id;
	public static volatile SingularAttribute<BusinessRent, Date> updatedAt;
	public static volatile SingularAttribute<BusinessRent, Date> createdAt;
}
