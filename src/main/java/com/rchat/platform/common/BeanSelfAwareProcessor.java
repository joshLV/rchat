package com.rchat.platform.common;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 防止内部方法直接调用，没有增强
 *
 * @author dzhang
 * @since 2017-02-27 17:14:58
 */
@Component
public class BeanSelfAwareProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof BeanSelfAware) {
			BeanSelfAware self = (BeanSelfAware) bean;
			self.setBeanSelf(bean);
		}

		return bean;
	}

}
