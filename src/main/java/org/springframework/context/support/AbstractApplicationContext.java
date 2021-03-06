package org.springframework.context.support;

import org.springframework.aop.Advisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * 负责实现刷新context的逻辑
 * 而factory的类型不负责
 */
public abstract class AbstractApplicationContext
        extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    public static final String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
    private ApplicationEventMulticaster eventMulticaster;
    private Thread hook;

    public abstract ConfigurableListableBeanFactory getBeanFactory();

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory ().getBean (name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return getBeanFactory ().containsBeanDefinition (name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory ().getBean (name, requiredType);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory ().getBeansOfType (type);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getBeanFactory ().getBean (requiredType);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory ().getBeanDefinitionNames ();
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        eventMulticaster.multicastEvent (event);
    }

    @Override
    public void refresh() throws BeansException {
        createBeanFactory ();
        ConfigurableListableBeanFactory factory = getBeanFactory ();

        //ApplicationContextAware是通过BeanPostProcesso完成的！
        //很多aware接口都是硬编码
        factory.addBeanPostProcessor (new ApplicationContextAwareProcessor (this));

        invokeBeanFactoryPostprocessors (factory);

        registerBeanPostProcessors (factory);

        createApplicationEventMultiCaster (factory);

        registerApplicationEventListeners (factory);

        initAdvisors (factory);

        //注册类型转换器和提前实例化单例bean
        finishBeanFactoryInitialization (factory);

        finishRefresh ();
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory factory) {
        //设置类型转换器
        if (factory.containsBeanDefinition (CONVERSION_SERVICE_BEAN_NAME)) {
            Object conversionService = factory.getBean (CONVERSION_SERVICE_BEAN_NAME);
            if (conversionService instanceof ConversionService) {
                factory.setConversionService ((ConversionService) conversionService);
            }
        }

        factory.preInstantiateSingletons ();
    }

    private void initAdvisors(ConfigurableListableBeanFactory factory) {
        factory.getBeansOfType (Advisor.class);
    }

    /**
     * 发布refresh完成事件
     */
    private void finishRefresh() {
        publishEvent (new ContextRefreshedEvent (this));
    }

    private void registerApplicationEventListeners(ConfigurableListableBeanFactory factory) {
        for (String name : factory.getBeanDefinitionNames ()) {
            BeanDefinition definition = factory.getBeanDefinition (name);
            if (ApplicationListener.class.isAssignableFrom (definition.getBeanClass ())) {
                eventMulticaster.addApplicationListener ((ApplicationListener<?>) getBean (name));
            }
        }
    }

    private void createApplicationEventMultiCaster(ConfigurableListableBeanFactory factory) {
        eventMulticaster = new SimpleApplicationEventMulticaster (factory);
        factory.addSingleton (APPLICATION_EVENT_MULTICASTER_BEAN_NAME, eventMulticaster);
    }

    /**
     * 不要一个一个add，否则就会导致第2个processor被第一个processor处理，以此类推
     */
    private void registerBeanPostProcessors(ConfigurableListableBeanFactory factory) {
        for (BeanPostProcessor processor :
                factory.getBeansOfType (BeanPostProcessor.class).values ()) {
            factory.addBeanPostProcessor (processor);
        }
    }

    /**
     * 类似的，也不要一个一个invoke
     */
    private void invokeBeanFactoryPostprocessors(ConfigurableListableBeanFactory factory) {
        for (BeanFactoryPostProcessor processor :
                factory.getBeansOfType (BeanFactoryPostProcessor.class).values ()) {
            processor.postProcessBeanFactory (factory);
        }
    }

    /**
     * 创建BeanFactory，并且加载xml配置
     */
    protected abstract void createBeanFactory();

    @Override
    public void close() {
        unRegisterShutdownHook ();
        doClose ();
    }

    protected void doClose() {
        publishEvent (new ContextClosedEvent (this));
        destroyBeans ();
    }

    @Override
    public void registerShutdownHook() {
        hook = new Thread (this::doClose);
        Runtime.getRuntime ().addShutdownHook (hook);
    }

    public void unRegisterShutdownHook() {
        Runtime.getRuntime ().removeShutdownHook (hook);
    }

    protected void destroyBeans() {
        getBeanFactory ().destroySingletons ();
    }
}
