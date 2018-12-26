package com.rchat.platform.common;

/**
 * 需要内部方法直接调用bean请集成此接口
 *
 * @author dzhang
 * @since 2017-02-27 17:15:44
 */
public interface BeanSelfAware<T> {
	void setBeanSelf(T self);
}
