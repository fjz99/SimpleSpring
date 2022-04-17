package org.springframework.beans.factory.support;

import cn.hutool.core.util.TypeUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.*;
import org.springframework.core.convert.ConversionService;

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
     * 在初始化后提前暴露bean引用
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object instance;
        instance = applyPostprocessBeforeInstantiation (beanDefinition.getBeanClass (), beanName);

        if (instance != null) {
            instance = applyBeanPostProcessorsAfterInitialization (instance, beanName);

            if (instance != null) {
                return instance;
            }
        }

        instance = createBeanInstance (beanDefinition);

        //提前暴露bean引用,原型bean的循环依赖无法解决
        if (beanDefinition.isSingleton ()) {
            setEarlyReference (beanName, instance);
        }

        boolean b = applyPostprocessAfterInstantiation (instance, beanName);
        if (!b) {
            return instance;
        }

        applyPostprocessPropertyValues (beanDefinition, instance, beanName);
        //依赖注入和属性设置
        injectDependencyAndSetPropertyValues (instance, beanDefinition);

        instance = initializeBean (beanName, beanDefinition, instance);

        if (beanDefinition.isSingleton ()) {
            //初始化完全完成后添加1级缓存
            //addSingleton会检查二级缓存和当前对象是否相同,等价于找是否被代理了。被代理了肯定不同
            addSingleton (beanName, instance);
            if (instance instanceof DisposableBean ||
                    !StringUtils.isBlank (beanDefinition.getDestroyMethodName ())) {
                addDisposableBean (beanName,
                        new DisposableBeanAdapter (beanName, beanDefinition.getDestroyMethodName (), instance));
            }

        }

        return instance;
    }

    protected void setEarlyReference(final String beanName, final Object instance) {
        addSingletonFactory (beanName, () -> {
            Object bean = instance;
            for (BeanPostProcessor processor : beanPostProcessors) {
                if (processor instanceof InstantiationAwareBeanPostProcessor) {
                    bean = ((InstantiationAwareBeanPostProcessor) processor).getEarlyBeanReference (bean, beanName);
                    if (bean == null) {
                        return null;
                    }
                }
            }
            return bean;
        });
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

    protected void injectDependencyAndSetPropertyValues(Object bean, BeanDefinition beanDefinition) {
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
            } else {
                //convert
                Class<?> source = value.getClass ();
                Class<?> target = (Class<?>) TypeUtil.getFieldType (bean.getClass (), propertyValue.getName ());
                ConversionService service = getConversionService ();
                if (service != null && service.canConvert (source, target)) {
                    value = service.convert (value, target);
                }
                //TODO log.warn?
            }

            //注入代理对象的问题：因为是基于子类或者基于接口的代理，才可以设置field，否则就类型不匹配
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

    protected Object applyPostprocessBeforeInstantiation(Class<?> beanClass, String beanName)
            throws BeansException {
        Object o;
        for (BeanPostProcessor processor : beanPostProcessors) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                o = ((InstantiationAwareBeanPostProcessor) processor).postProcessBeforeInstantiation (beanClass, beanName);

                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }

    /**
     * @return 是否要进行依赖注入和属性设置
     */
    protected boolean applyPostprocessAfterInstantiation(Object bean, String beanName) throws BeansException {
        boolean b;
        for (BeanPostProcessor processor : beanPostProcessors) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                b = ((InstantiationAwareBeanPostProcessor) processor).postProcessAfterInstantiation (bean, beanName);

                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void applyPostprocessPropertyValues(BeanDefinition definition, Object existingBean, String beanName)
            throws BeansException {
        PropertyValues propertyValues = definition.getPropertyValues ();
        for (BeanPostProcessor processor : beanPostProcessors) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                propertyValues =
                        ((InstantiationAwareBeanPostProcessor) processor).postProcessPropertyValues (propertyValues, existingBean, beanName);
            }
        }

        if (propertyValues != null) {
            definition.setPropertyValues (propertyValues);
        }
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
