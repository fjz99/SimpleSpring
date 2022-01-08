package org.springframework.beans.factory.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;


public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private final Map<String, Object> singletons = new HashMap<> ();
    /**
     * 用于适配器
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<> ();

    @Override
    public Object getSingleton(String beanName) {
        return singletons.get (beanName);
    }

    @Override
    public void addSingleton(String beanName, Object singletonObject) {
        singletons.put (beanName, singletonObject);
    }

    public void addDisposableBean(String name, DisposableBean bean) {
        disposableBeans.put (name, bean);
    }

    /**
     * 通过继承实现特有的接口
     * destroySingletons确实应该放到单例注册表里
     */
    public void destroySingletons() {
        disposableBeans.forEach ((k, o) -> {
            try {
                o.destroy ();
                disposableBeans.remove (k);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        });
    }

}
