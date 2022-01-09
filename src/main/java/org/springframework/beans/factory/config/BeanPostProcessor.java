package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 用于修改实例化后的bean的修改扩展点
 */
public interface BeanPostProcessor {

	/**
	 * 在bean执行初始化方法之前执行此方法
	 */
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException{
		return bean;
	}

	/**
	 * 在bean执行初始化方法之后执行此方法
	 */
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
		return bean;
	}
}
