package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * BeanFactory是内部使用的
 * appContext是外部使用的
 * appContext组合了一个BeanFactory
 */
public interface BeanFactory {

    /**
     * 获取bean
     *
     * @throws BeansException bean不存在时
     */
    Object getBean(String name) throws BeansException;

    /**
     * 根据名称和类型查找bean
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    <T> T getBean(Class<T> requiredType) throws BeansException;

}
