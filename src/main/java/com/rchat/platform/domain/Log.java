package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日志实体，记录谁什么时候对什么资源做了什么操作
 *
 * @author dzhang
 */
@Entity
@Table(name = "t_log")
public class Log extends TimestampedResource implements Serializable {

    private static final long serialVersionUID = 8943213082631560821L;
    /**
     * 日志Id
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    /**
     * 操作员
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"roles"})
    private User operator;

    /**
     * 资源
     */
    private String resource;
    /**
     * 操作
     */
    private String operation;

    @Enumerated(EnumType.ORDINAL)
    private OperationStatus status;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LogDetail> details;
    @Column(columnDefinition = "text")
    private String message;
    @Transient
    @JsonIgnore
    private AtomicInteger stepGenerator = new AtomicInteger(1);
    private String url;

    public Log() {
    }

    public Log(User operator) {
        super();
        this.operator = operator;
    }

    public Log(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<LogDetail> getDetails() {
        if (details == null) {
            details = new ArrayList<>();
        }
        return details;
    }

    public void setDetails(List<LogDetail> details) {
        this.details = details;
    }

    public void addDetail(LogDetail detail) {
        detail.setStep(stepGenerator.getAndIncrement());
        getDetails().add(detail);
        detail.setLog(this);
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("Log [operator=%s, resource=%s, operation=%s, status=%s]", operator, resource, operation,
                status);
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
        if (!(obj instanceof Log))
            return false;
        Log other = (Log) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
