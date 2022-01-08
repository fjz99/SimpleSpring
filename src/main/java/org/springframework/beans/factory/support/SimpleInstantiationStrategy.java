package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.InvocationTargetException;

/**
 * 直接实例化
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        try {
            return beanDefinition.getBeanClass ().getConstructor ().newInstance ();
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }
}
