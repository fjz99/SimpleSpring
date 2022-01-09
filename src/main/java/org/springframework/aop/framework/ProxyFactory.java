package org.springframework.aop.framework;

import org.springframework.aop.AdvisedSupport;

/**
 * 简单工厂模式
 * 根据代理类型返回正确的代理对象
 */
public class ProxyFactory {

    private final AdvisedSupport support;

    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.support = advisedSupport;
    }

    private AopProxy createAopProxy() {
        if (support.isProxyTargetClass ()) {
            return new CglibAopProxy (support);
        } else return new JdkDynamicAopProxy (support);
    }

    public Object getProxy() {
        return createAopProxy ().getProxy ();
    }
}
