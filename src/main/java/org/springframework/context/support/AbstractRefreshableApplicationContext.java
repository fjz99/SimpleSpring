package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 相比父类，维护了一个bean factory
 *
 * @date 2022/1/8 16:08
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    private DefaultListableBeanFactory factory;

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return factory;
    }

    @Override
    protected void createBeanFactory() {
        factory = new DefaultListableBeanFactory ();
        loadBeanDefinitions (factory);
    }

    //因为加载bean定义可能有多种方式，比如xml
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException;

}
