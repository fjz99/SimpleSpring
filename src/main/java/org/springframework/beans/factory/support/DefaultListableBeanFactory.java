package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @date 2022/1/8 14:14
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<> ();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitions.put (beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        return Optional
                .ofNullable (beanDefinitions.get (beanName))
                .orElseThrow (BeansException::new);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitions.containsKey (beanName);
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        beanDefinitions.forEach ((k, v) -> {
            if (v.isSingleton ()) {
                getBean (k);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> map = new HashMap<> ();

        beanDefinitions.forEach ((k, v) -> {
            if (type.isAssignableFrom (v.getBeanClass ())) {
                map.put (k, (T) getBean (k));
            }
        });
        return map;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitions.keySet ().toArray (new String[0]);
    }

}
