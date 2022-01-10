package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.StringValueResolver;

import java.util.*;

/**
 * @date 2022/1/8 14:14
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<> ();
    private final List<StringValueResolver> resolvers = new ArrayList<> ();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitions.put (beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        return Optional
                .ofNullable (beanDefinitions.get (beanName))
                .orElseThrow (() -> new BeansException ("bean " + beanName + " 不存在"));
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

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        resolvers.add (valueResolver);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        for (StringValueResolver resolver : resolvers) {
            value = resolver.resolveStringValue (value);
        }
        return value;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        List<String> beanNames = new ArrayList<> ();
        for (String name : getBeanDefinitionNames ()) {
            //这样才能保证接口好使
            if (requiredType.isAssignableFrom (getBeanDefinition (name).getBeanClass ())) {
                beanNames.add (name);
            }
        }

        if (beanNames.size () == 1) {
            return getBean (beanNames.get (0), requiredType);
        }

        throw new BeansException (requiredType + "expected single bean but found " +
                beanNames.size () + ": " + beanNames);
    }

}
