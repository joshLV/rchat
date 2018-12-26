package com.rchat.platform.service;

import java.util.List;

import com.rchat.platform.domain.BusinessRent;
import com.rchat.platform.domain.TalkbackUser;

/**
 * 租用业务信息接口
 * 
 * @author dzhang
 *
 */
public interface BusinessRentService extends GenericService<BusinessRent, String> {

	/**
	 * 根据对讲账号查找租用业务信息
	 * 
	 * @param user
	 * @return
	 */
	List<BusinessRent> findByTalkbackUser(TalkbackUser user);

}
