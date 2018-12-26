package com.rchat.platform.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 视频服务器信息实体
 */
@Entity
@Table(name = "t_server")
public class Server implements Serializable {
    private static final long serialVersionUID = -3712433403508848326L;
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    @Column(length = 100)
    private String name;
    private ServerStatus status;
    private String domain;
    private String ipAddress;
    private long maxCapacity;
    /**
     * 剩余容量不存储，需要的时候，直接数据库查询放在这个服务器上的集团对讲账号总数
     */
    @Transient
    private long remanentCapacity = -1;

    public Server() {
    }

    public Server(String id) {
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

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public long getRemanentCapacity() {
        return remanentCapacity;
    }

    public void setRemanentCapacity(long remanentCapacity) {
        this.remanentCapacity = remanentCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        return id != null ? id.equals(server.id) : server.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Server{id='%s', name='%s', status=%s, domain='%s', ipAddress='%s', maxCapacity=%d, currentCapacity=%d}", id, name, status, domain, ipAddress, maxCapacity, remanentCapacity);
    }

    @Transient
    public void reduceRemanetCapacity(long additionalSize) {
        this.remanentCapacity -= additionalSize;
    }
}
