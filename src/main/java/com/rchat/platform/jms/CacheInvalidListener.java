package com.rchat.platform.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 缓存失效
 * 
 * @author dzhang
 *
 */
@Component
public class CacheInvalidListener {
	@Autowired
	private CacheManager cacheMgr;

	@JmsListener(destination = RchatMQConfiguration.RCHAT_CACHE_QUEUE)
	public void onCacheInvalid(RchatMessage msg) {
		System.err.println(msg);
		Cache cache = cacheMgr.getCache("authority");
		cache.clear();
	}
}
