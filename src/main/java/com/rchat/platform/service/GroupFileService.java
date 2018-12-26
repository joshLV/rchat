package com.rchat.platform.service;

import com.rchat.platform.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 集团业务
 *
 * @author dzhang
 * @since 2017-04-04 20:25:20
 */
public interface GroupFileService extends GenericService<GroupFile, String> {
	
	List<GroupFile> findByFileId(String fileId);
	
	List<GroupFile> findByGroupAndType(Group group, FileType type);
}
