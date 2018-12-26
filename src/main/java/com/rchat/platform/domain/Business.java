package com.rchat.platform.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 业务实体
 *
 * @author dzhang
 * @since 2017-04-04 16:49:03
 */
@Entity
@Table(name = "t_business")
public class Business extends TimestampedResource implements Serializable, Comparable<Business> {

    private static final long serialVersionUID = 1L;
    /**
     * 自增长Id
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    /**
     * 业务名称
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    /**
     * 业务代码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    /**
     * 收费标准（点/月）
     */
    @Column(scale = 2)
    private long creditPerMonth;
    /**
     * 业务描述
     */
    @Column
    private String description;
    /**
     * 是否是默认业务
     */
    @Column
    private Boolean internal = Boolean.FALSE;
    /**
     * 业务使能
     */
    @Column
    private Boolean enabled = Boolean.TRUE;

    public Business() {
    }

    public Business(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreditPerMonth() {
        return creditPerMonth;
    }

    public void setCreditPerMonth(long creditPerMonth) {
        this.creditPerMonth = creditPerMonth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public boolean isInternal() {
        return internal;
    }

    @Override
    public int compareTo(Business o) {
        return name.compareToIgnoreCase(o.name);
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Business business = (Business) o;

        return id != null ? id.equals(business.id) : business.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Business [id=%s, name=%s, code=%s, creditPerMonth=%s, description=%s]", id, name, code,
                creditPerMonth, description);
    }

}
