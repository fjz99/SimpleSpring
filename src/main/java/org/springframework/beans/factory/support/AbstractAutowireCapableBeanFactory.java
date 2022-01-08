package org.springframework.beans.factory.support;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.BeanReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 增加了autowire能力
 * 增加了bean postprocessor能力
 * 除了bean definition的维护，其他都实现了
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy ();

    /**
     * 具体创建一个bean
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object instance = createBeanInstance (beanDefinition);

        //依赖注入
        injectDependency (instance, beanDefinition);

        instance = initializeBean (beanName, beanDefinition, instance);

        if (beanDefinition.isSingleton ()) {
            addSingleton (beanName, instance);
            if (instance instanceof DisposableBean ||
                    !StringUtils.isBlank (beanDefinition.getDestroyMethodName ())){
                addDisposableBean (beanName,
                        new DisposableBeanAdapter (beanName, beanDefinition.getDestroyMethodName (), instance));
            }

        }

        return instance;
    }

    protected Object initializeBean(String beanName, BeanDefinition beanDefinition, Object instance) {
        //BeanFactoryAware当然由BeanFactory来处理
        if (instance instanceof BeanFactoryAware) {
            ((BeanFactoryAware) instance).setBeanFactory (this);
        }

        instance = applyBeanPostProcessorsBeforeInitialization (instance, beanName);
        applyLifecycleMethods (instance, beanDefinition);
        instance = applyBeanPostProcessorsAfterInitialization (instance, beanName);
        return instance;
    }

    protected void applyLifecycleMethods(Object bean, BeanDefinition definition) {
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet ();
            } catch (Exception e) {
                e.printStackTrace ();
                throw new BeansException ("", e);
            }
        }

        try {
            String initMethodName = definition.getInitMethodName ();
            if (initMethodName == null) {
                return;
            }
            Method method = bean.getClass ().getMethod (initMethodName);
            method.invoke (bean);
        } catch (NoSuchMethodException e) {
            throw new AssertionError (e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace ();
            throw new BeansException ("", e);
        }
    }

    protected void injectDependency(Object bean, BeanDefinition beanDefinition) {
        PropertyValue[] values = beanDefinition.getPropertyValues ().getPropertyValues ();
        for (PropertyValue propertyValue : values) {
            Object value = propertyValue.getValue ();
            if (value instanceof BeanReference) {
                //bean依赖
                String beanName = ((BeanReference) value).getBeanName ();
                value = getBean (beanName);
                if (value == null) {
                    throw new BeansException ("找不到bean " + beanName);
                }
            }
            try {
                BeanUtils.setProperty (bean, propertyValue.getName (), value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace ();
                throw new BeansException ("set bean field " + propertyValue.getName () + " 失败", e);
            }
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition) {
        if (getInstantiationStrategy () == null) {
            throw new IllegalStateException ();
        }

        return getInstantiationStrategy ().instantiate (beanDefinition);
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        for (BeanPostProcessor processor : beanPostProcessors) {
            existingBean = processor.postProcessBeforeInitialization (existingBean, beanName);
            if (existingBean == null) {
                return null;
            }
        }
        return existingBean;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        for (BeanPostProcessor processor : beanPostProcessors) {
            existingBean = processor.postProcessAfterInitialization (existingBean, beanName);
            if (existingBean == null) {
                return null;
            }
        }
        return existingBean;
    }

}
