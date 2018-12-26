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
 * 集团文件实体
 *
 * @author wys
 */
@Entity
@Table(name = "t_group_file")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupFile extends TimestampedResource implements Serializable, Comparable<GroupFile> {
    private static final long serialVersionUID = 8509232620859115647L;
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    /**
     * 文件名称
     */
    @Column(nullable = false, length = 100)
    @NotNull
    private String name;
    /**
     * 文件标识
     */
    @Column(length = 100)
    private String fileId;
    /**
     * 文件路径
     */
    @Column(length = 255)
    private String url;

    /**
     * 所属集团，如果group_id 不为空，说明是集团直属的部门
     */
    @ManyToOne
    @JoinColumn(name = "group_id", updatable = false)
    @JsonIgnoreProperties({"agent", "administrator", "creator", "businesses"})
    @NotNull
    private Group group;

    /**
     * 文件大小
     */
    private int size = 0;
    
    /**
     * 文件类型
     */
    private FileType type = FileType.File;

    public GroupFile() {
    	
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

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	@Override
    public String toString() {
        return String.format("GroupFile [id=%s, name=%s, size=%s, parent=%s]", id, name, size);
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
        if (!(obj instanceof GroupFile))
            return false;
        GroupFile other = (GroupFile) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int compareTo(GroupFile o) {
        return name.compareTo(o.name);
    }

}
