package org.springframework.aop.framework;

import org.springframework.aop.AdvisedSupport;
import org.springframework.aop.TargetSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于jdk动态代理的代理对象
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private final Object proxy;
    private final AdvisedSupport support;
    private final TargetSource targetSource;

    public JdkDynamicAopProxy(AdvisedSupport support) {
        this.support = support;
        targetSource = support.getTarget ();
        proxy = createProxy ();
    }

    private Object createProxy() {
        ClassLoader classLoader = targetSource.getTarget ().getClass ().getClassLoader ();
        return Proxy.newProxyInstance (classLoader, targetSource.getTargetInterfaces (), this);
    }

    /**
     * jdk动态代理实现这个{@link InvocationHandler}接口
     * 然后执行方法会自动被这个方法拦截
     * 这是基于接口的动态代理，即生成的代理类实现了{@link TargetSource#getTargetInterfaces()}对应的接口
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (support.getMethodMatcher ().matches (method, targetSource.getClass ())) {
            return support.getMethodInterceptor ().invoke (new ReflectiveMethodInvocation (method, args, targetSource.getTarget ()));
        }
        //使用targetSource，执行真实的方法，否则就会死循环
        return method.invoke (targetSource.getTarget (), args);
    }

    @Override
    public Object getProxy() {
        return proxy;
    }
}
