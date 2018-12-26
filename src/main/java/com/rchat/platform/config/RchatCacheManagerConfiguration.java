package com.rchat.platform.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RchatCacheManagerConfiguration {
	private static final Log log = LogFactory.getLog(RchatCacheManagerConfiguration.class);

	@Bean
	public CacheManager cacheManager() {
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		try {
			cacheManager.setCacheManager(ehcachCacheManagerFactory().getObject());
		} catch (Exception e) {
			log.error(e);
		}
		return cacheManager;
	}

	@Bean(destroyMethod = "destroy")
	public FactoryBean<net.sf.ehcache.CacheManager> ehcachCacheManagerFactory() {
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
		factory.setShared(true);
		return factory;
	}

}
