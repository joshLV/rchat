package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 代理商信息
 *
 * @author dzhang
 */
@Entity
@Table(name = "t_agent", indexes = {@Index(columnList = "name", unique = true)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Agent extends TimestampedResource implements Serializable, Comparable<Agent>, Loggable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    /**
     * 代理商名称
     */
    @Column(length = 100, nullable = false)
    @NotNull
    @Size(min = 2, max = 100)
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
     * 代理区域
     */
    @Column(length = 100)
    private String region;

    /**
     * 额度单价
     */
    @Column(scale = 2)
    private Double creditUnitPrice;
    /**
     * 上级代理，如果是平台，则没有上级代理
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", updatable = false)
    @JsonIgnoreProperties({"administrator", "creator", "children"})
    private Agent parent;
    /**
     * 下级代理商
     */
    @Transient
    @JsonIgnoreProperties({"creator"})
    @JsonProperty(access = Access.READ_ONLY)
    private List<Agent> children;

    /**
     * 直属集团
     */
    @Transient
    @JsonIgnoreProperties({"administrator", "creator"})
    @JsonProperty(access = Access.READ_ONLY)
    private List<Group> groups;

    /**
     * 管理员
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "administrator", updatable = false)
    private User administrator;
    /**
     * 创建者
     */
    @ManyToOne
    @JoinColumn(name = "creator", nullable = false)
    private User creator;
    /**
     * 服务器ID
     */
    private String  serverId;

    /**
     * 代理商类型
     */
    @Enumerated(EnumType.ORDINAL)
    private AgentType type = AgentType.NORMAL;

    /**
     * 号段
     */
    @Transient
    @JsonProperty(access = Access.READ_ONLY)
    private List<AgentSegment> segments;

    /**
     * 代理商等级
     */
    private int level;

    @Transient
    private AgentSegment segment;
    @Transient
    private Long userAmount;
    @Transient
    private Long nonactiveUserAmount;
    @Transient
    private Long expiredUserAmount;
    @Transient
    private Long expiringUserAmount;
    /**
     * 剩余额度
     */
    @Transient
    private Long creditRemaint;
    /**
     * 累计额度
     */
    @Transient
    private Long creditAccumulation;

    public Agent() {
    }

    public Agent(String id) {
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

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double getCreditUnitPrice() {
        return creditUnitPrice;
    }

    public void setCreditUnitPrice(Double creditUnitPrice) {
        this.creditUnitPrice = creditUnitPrice;
    }

    public Agent getParent() {
        return parent;
    }

    public void setParent(Agent parent) {
        this.parent = parent;
    }

    public List<Agent> getChildren() {
        return children;
    }

    public void setChildren(List<Agent> children) {
        this.children = children;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
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

    public List<AgentSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<AgentSegment> segments) {
        this.segments = segments;
    }

    public AgentSegment getSegment() {
        return segment;
    }

    public void setSegment(AgentSegment segment) {
        this.segment = segment;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public Long getCreditRemaint() {
        return creditRemaint;
    }

    public void setCreditRemaint(Long creditRemaint) {
        this.creditRemaint = creditRemaint;
    }

    public Long getCreditAccumulation() {
        return creditAccumulation;
    }

    public void setCreditAccumulation(Long creditAccumulation) {
        this.creditAccumulation = creditAccumulation;
    }

    @Override
    public int compareTo(Agent o) {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Agent [id=%s, name=%s, linkman=%s, phone=%s, email=%s, region=%s]", id, name, linkman,
                phone, email, region);
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
        if (!(obj instanceof Agent))
            return false;
        Agent other = (Agent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public LogDetail toLogDetail() {
        LogDetail detail = new LogDetail();
        detail.setResourceId(String.valueOf(id));
        detail.setResourceName(String.valueOf(name));
        detail.setTableName("t_agent");

        return detail;
    }

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}


}
