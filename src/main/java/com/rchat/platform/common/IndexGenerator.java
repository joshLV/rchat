package com.rchat.platform.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rchat.platform.config.IndexProperties;

/**
 * 每次启动的时候，重新生成索引
 * 
 * @author dzhang
 *
 */
@Component
public class IndexGenerator implements InitializingBean {
	private static final Log log = LogFactory.getLog(IndexGenerator.class);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private IndexProperties indexProperties;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (indexProperties.isEnabled()) {
			log.info("生成索引！！！");
		}
	}
}
