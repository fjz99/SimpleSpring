package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * 增加了加载BeanDefinition的功能
 *
 * @date 2022/1/8 16:11
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException {
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader (beanFactory, this);
        reader.loadBeanDefinitions (getConfigLocations ());
    }

    protected abstract String[] getConfigLocations();

}
