package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 骨架类，只提供了createBean，getBeanDefinition
 */
public abstract class AbstractBeanFactory
        extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    protected final List<BeanPostProcessor> beanPostProcessors = new ArrayList<> ();

    private final Map<String, Object> factoryBeanObjectCache = new HashMap<> ();

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 获得bean，如果不存在就创建
     * 假如是factorybean的话，singleton map存放的还是factory
     * factory的生成bean的缓存是单独的 factoryBeanObjectCache
     * 这样可以保存曾经是factory bean的信息
     */
    @Override
    public Object getBean(String name) throws BeansException {
        Object singleton = getSingleton (name);
        if (singleton == null) {
            BeanDefinition definition = getBeanDefinition (name);
            singleton = createBean (name, definition);
        }
        return getBeanUsingFactoryBean (name, singleton);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getBean (name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return containsBeanDefinitionInternal (name);
    }

    protected abstract boolean containsBeanDefinitionInternal(String beanName);

    /**
     * 可能会用factory bean
     */
    private Object getBeanUsingFactoryBean(String name, Object o) throws BeansException {
        if (o instanceof FactoryBean) {
            FactoryBean<?> bean = (FactoryBean<?>) o;
            if (bean.isSingleton () && factoryBeanObjectCache.containsKey (name)) {
                o = factoryBeanObjectCache.get (name);
            } else {
                try {
                    o = bean.getObject ();
                } catch (Exception e) {
                    //因为getObject ()要给用户用，自然就需要throws Exception
                    e.printStackTrace ();
                    throw new BeansException ("", e);
                }

                if (bean.isSingleton ()) {
                    factoryBeanObjectCache.put (name, o);
                }
            }
        }
        return o;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.remove (beanPostProcessor);
        beanPostProcessors.add (beanPostProcessor);
    }

}
