package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 集团
 *
 * @author dzhang
 * @since 2017-02-27 16:45:26
 */
@Entity
@Table(name = "t_group", indexes = {@Index(columnList = "name", unique = true)})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Group extends TimestampedResource implements Serializable, Comparable<Group>, Loggable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    /**
     * 集团名称
     */
    @Column(length = 100, nullable = false)
    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    /**
     * VIP集团
     */
    private boolean vip;

    /**
     * 管理员
     */
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name = "administrator", updatable = false)
    @JsonIgnoreProperties({"roles"})
    private User administrator;

    /**
     * 所属代理商
     */
    @ManyToOne
    @JoinColumn(name = "agent_id", updatable = false)
    @JsonIgnoreProperties({"administrator", "creator", "children", "parent"})
    @NotNull
    private Agent agent;

    /**
     * 创建者
     */
    @ManyToOne
    @JoinColumn(name = "creator", updatable = false)
    @JsonIgnoreProperties({"roles"})
    private User creator;

    /**
     * 额度单价
     */
    @Column(scale = 2)
    private Double creditUnitPrice;

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
     * 集团规模
     */
    @Enumerated(EnumType.ORDINAL)
    private GroupScale scale;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "t_group_business", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id",
            "business_id"}), joinColumns = {@JoinColumn(nullable = false, name = "group_id")}, inverseJoinColumns = {
            @JoinColumn(nullable = false, name = "business_id")})
    @Fetch(FetchMode.SUBSELECT)
    private List<Business> businesses;
    /**
     * 集团直属部门
     */
    @Transient
    @JsonProperty(access = Access.READ_ONLY)
    private List<Department> departments;

    @Transient
    @JsonProperty(access = Access.READ_ONLY)
    private List<GroupSegment> segments;

    @Transient
    private GroupSegment segment;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Transient
    private Long userAmount;
    @Transient
    private Long nonactiveUserAmount;
    @Transient
    private Long expiredUserAmount;
    @Transient
    private Long expiringUserAmount;
    @Transient
    private Long creditAccumulation;
    @Transient
    private Long creditRemaint;
    
    /**
     * 是否开启存储空间
     */
    private boolean status;
    
    /**
     * 存储总空间
     */
    @Min(0)
    private long totalSpace;
    
    /**
     * 已用空间
     */
    @Min(0)
    private long usedSpace;
    
    /**
     * 存储空间到期时间
     */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date deadline;


    public Group() {
    }

    public Group(String id) {
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

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public List<GroupSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<GroupSegment> segments) {
        this.segments = segments;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Double getCreditUnitPrice() {
        return creditUnitPrice;
    }

    public void setCreditUnitPrice(Double creditUnitPrice) {
        this.creditUnitPrice = creditUnitPrice;
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

    public GroupScale getScale() {
        return scale;
    }

    public void setScale(GroupScale scale) {
        this.scale = scale;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public GroupSegment getSegment() {
        return segment;
    }

    public void setSegment(GroupSegment segment) {
        this.segment = segment;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
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

    public Long getCreditAccumulation() {
        return creditAccumulation;
    }

    public void setCreditAccumulation(Long creditAccumulation) {
        this.creditAccumulation = creditAccumulation;
    }

    public Long getCreditRemaint() {
        return creditRemaint;
    }

    public void setCreditRemaint(Long creditRemaint) {
        this.creditRemaint = creditRemaint;
    }
    
    public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}
	
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Override
    public String toString() {
        return String.format("Group [id=%s, name=%s, description=%s]", id, name, description);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int compareTo(Group o) {
        return name.compareTo(o.name);
    }

    @Override
    public LogDetail toLogDetail() {
        LogDetail detail = new LogDetail();
        detail.setResourceId(String.valueOf(id));
        detail.setResourceName(name);
        detail.setTableName("t_group");
        return detail;
    }
}
