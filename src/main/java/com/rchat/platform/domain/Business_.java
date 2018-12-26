package com.rchat.platform.domain;

import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Business.class)
public class Business_ extends TimestampedResource_ {
    public static volatile SingularAttribute<Business, String> id;
    public static volatile SingularAttribute<Business, Boolean> enabled;
    public static volatile SingularAttribute<Business, Boolean> internal;
}
