package org.springframework.context.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @date 2022/1/8 15:38
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    public SimpleApplicationEventMulticaster(BeanFactory factory) {
        setBeanFactory (factory);
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener<ApplicationEvent> listener : listeners) {
            if (supports (listener, event)) {
                try {
                    listener.onApplicationEvent (event);
                } catch (Throwable e) {
                    e.printStackTrace ();
                }
            }
        }
    }

    private boolean supports(ApplicationListener<?> listener, ApplicationEvent event) {
        try {
            Type type = listener.getClass().getGenericInterfaces()[0];
            Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
            String className = actualTypeArgument.getTypeName();
            Class<?> eventClassName;
            try {
                eventClassName = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeansException ("wrong event class name: " + className);
            }

            return eventClassName.isAssignableFrom (event.getClass ());
        } catch (Throwable e) {
            e.printStackTrace ();
            return false;
        }

    }

}
