package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;


public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private final Map<String, Object> singletonObjects = new HashMap<> ();
    private final Map<String, Object> earlySingletonObjects = new HashMap<> ();
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<> ();
    /**
     * 用于适配器
     */
    private final Map<String, DisposableBean> disposableBeans = new ConcurrentHashMap<> ();

    /**
     * 每一级缓存逐级检查，3级缓存提升到2级
     * 但是2级不会提升到一级，2级到1级在完全初始化bean完成后才进行
     */
    @Override
    public Object getSingleton(String beanName) {
        if (!singletonObjects.containsKey (beanName)) {
            if (!earlySingletonObjects.containsKey (beanName)) {
                if (!singletonFactories.containsKey (beanName)) {
                    return null;
                } else {
                    Object o = singletonFactories.get (beanName).getObject ();
                    earlySingletonObjects.put (beanName, o);
                    singletonFactories.remove (beanName);
                    return o;
                }
            } else return earlySingletonObjects.get (beanName);
        } else return singletonObjects.get (beanName);
    }

    /**
     * 会检查二级缓存和当前对象是否相同
     */
    @Override
    public void addSingleton(String beanName, Object singletonObject) {
        if (earlySingletonObjects.containsKey (beanName) &&
                earlySingletonObjects.get (beanName) != singletonObject) {
            throw new BeansException ("无法解决此类关于代理的循环依赖");
        }

        singletonObjects.put (beanName, singletonObject);
        singletonFactories.remove (beanName);
        earlySingletonObjects.remove (beanName);
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> factory) {
        singletonFactories.put (beanName, factory);
    }

    public void addDisposableBean(String name, DisposableBean bean) {
        disposableBeans.put (name, bean);
    }

    /**
     * 通过继承实现特有的接口
     * destroySingletons确实应该放到单例注册表里
     * remove是保证close之后shutdown hook执行的时候不会重复执行o.destroy ();
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
