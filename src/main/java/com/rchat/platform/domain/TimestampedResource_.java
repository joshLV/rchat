package com.rchat.platform.domain;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TimestampedResource.class)
public class TimestampedResource_ {
	public static volatile SingularAttribute<TimestampedResource, Date> createdAt;
	public static volatile SingularAttribute<TimestampedResource, Date> updatedAt;
}
