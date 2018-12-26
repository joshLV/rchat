package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 部门实体，公司的组织架构就是由部门构成的，公司也是部门
 *
 * @author dzhang
 */
@Entity
@Table(name = "t_department")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Department extends TimestampedResource implements Serializable, Comparable<Department> {
    private static final long serialVersionUID = 8509232620859115647L;
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    /**
     * 部门名称
     */
    @Column(nullable = false, length = 100)
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    /**
     * 联系人
     */
    @Column(length = 100)
    private String linkman;
    /**
     * 联系电话
     */
    @Column(length = 100)
    private String phone;
    /**
     * 联系邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 所属部门
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "parent", updatable = false)
    @JsonIgnoreProperties({"group", "administrator", "creator", "parent"})
    private Department parent;

    /**
     * 所属集团，如果group_id 不为空，说明是集团直属的部门
     */
    @ManyToOne
    @JoinColumn(name = "group_id", updatable = false)
    @JsonIgnoreProperties({"agent", "administrator", "creator", "businesses"})
    @NotNull
    private Group group;
    /**
     * 管理员
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "administrator", nullable = false, updatable = false)
    @JsonIgnoreProperties({"roles"})
    @NotNull
    private User administrator;
    /**
     * 创建者
     */
    @ManyToOne
    @JoinColumn(name = "creator", updatable = false)
    @JsonIgnoreProperties({"roles"})
    private User creator;

    @Transient
    @JsonIgnoreProperties({"creator"})
    @JsonProperty(access = Access.READ_ONLY)
    private List<Department> children;

    /**
     * 部门权限
     */
    @Embedded
    @NotNull
    private DepartmentPrivilege privilege;

    /**
     * 默认群组
     */
    @JsonIgnore
    @Transient
    private TalkbackGroup defaultGroup;

    // 默认是一级部门，也就是集团直属部门
    private int level = 1;
    @Transient
    private Long userAmount;
    @Transient
    private Long nonactiveUserAmount;
    @Transient
    private Long expiredUserAmount;
    @Transient
    private Long expiringUserAmount;

    public Department() {
    }

    public Department(String id) {
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

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getParent() {
        return parent;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }

    public User getAdministrator() {
        return administrator;
    }

    public void setAdministrator(User administrator) {
        this.administrator = administrator;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Department> getChildren() {
        return children;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }

    public DepartmentPrivilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(DepartmentPrivilege privilege) {
        this.privilege = privilege;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public TalkbackGroup getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(TalkbackGroup defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(Long userAmount) {
        this.userAmount = userAmount;
    }

    public Long getNonactiveUserAmount() {
        return nonactiveUserAmount;
    }

    public void setNonactiveUserAmount(Long nonactiveUserAmount) {
        this.nonactiveUserAmount = nonactiveUserAmount;
    }

    public Long getExpiredUserAmount() {
        return expiredUserAmount;
    }

    public void setExpiredUserAmount(Long expiredUserAmount) {
        this.expiredUserAmount = expiredUserAmount;
    }

    public Long getExpiringUserAmount() {
        return expiringUserAmount;
    }

    public void setExpiringUserAmount(Long expiringUserAmount) {
        this.expiringUserAmount = expiringUserAmount;
    }

    @Override
    public String toString() {
        return String.format("Department [id=%s, name=%s, linkman=%s, parent=%s]", id, name, linkman, parent);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Department))
            return false;
        Department other = (Department) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int compareTo(Department o) {
        return name.compareTo(o.name);
    }

}
