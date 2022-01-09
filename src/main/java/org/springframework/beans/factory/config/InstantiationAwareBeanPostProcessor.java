package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;


public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 在bean实例化之前执行
     *
     * @return 创建的实例，返回null则走正常流程
     */
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * bean实例化之后，设置属性之前执行
     *
     * @return 是否要继续执行依赖注入和属性设置
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    /**
     * bean实例化之后，设置属性之前执行
     */
    default PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {
        return pvs;
    }

}
