package org.springframework.context.support;

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
    private ApplicationEventMulticaster eventMulticaster;

    public abstract ConfigurableListableBeanFactory getBeanFactory();

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory ().getBean (name);
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
        factory.addBeanPostProcessor (new ApplicationContextAwareProcessor (this));

        invokeBeanFactoryPostprocessor (factory);

        registerBeanPostProcessor (factory);

        createApplicationEventMultiCaster (factory);

        registerApplicationEventListeners (factory);

        factory.preInstantiateSingletons ();

        finishRefresh ();
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

    private void registerBeanPostProcessor(ConfigurableListableBeanFactory factory) {
        for (String name : factory.getBeanDefinitionNames ()) {
            BeanDefinition definition = factory.getBeanDefinition (name);
            if (BeanPostProcessor.class.isAssignableFrom (definition.getBeanClass ())) {
                factory.addBeanPostProcessor ((BeanPostProcessor) getBean (name));
            }
        }
    }

    private void invokeBeanFactoryPostprocessor(ConfigurableListableBeanFactory factory) {
        for (String name : factory.getBeanDefinitionNames ()) {
            BeanDefinition definition = factory.getBeanDefinition (name);
            if (BeanFactoryPostProcessor.class.isAssignableFrom (definition.getBeanClass ())) {
                ((BeanFactoryPostProcessor) getBean (name)).postProcessBeanFactory (factory);
            }
        }
    }

    /**
     * 创建BeanFactory，并且加载xml配置
     */
    protected abstract void createBeanFactory();

    @Override
    public void close() {
        doClose ();
    }

    protected void doClose() {
        publishEvent (new ContextClosedEvent (this));
        destroyBeans ();
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime ().addShutdownHook (new Thread () {
            @Override
            public void run() {
                doClose ();
            }
        });
    }

    protected void destroyBeans() {
        getBeanFactory ().destroySingletons ();
    }
}
