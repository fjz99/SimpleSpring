package org.springframework.context.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @date 2022/1/8 15:35
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {

    protected final List<ApplicationListener<ApplicationEvent>> listeners = new CopyOnWriteArrayList<> ();
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public void addApplicationListener(ApplicationListener<?> listener) {
        removeApplicationListener (listener);
        listeners.add ((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        listeners.remove (listener);
    }

}
