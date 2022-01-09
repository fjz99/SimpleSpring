package org.springframework.aop.framework;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.aop.AdvisedSupport;

import java.lang.reflect.Method;

/**
 * cglib动态代理
 * copy
 */
public class CglibAopProxy implements AopProxy {

    private final AdvisedSupport advised;

    public CglibAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }


    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer ();
        enhancer.setSuperclass (advised.getTarget ().getTarget ().getClass ());
        enhancer.setInterfaces (advised.getTarget ().getTargetInterfaces ());
        enhancer.setCallback (new DynamicAdvisedInterceptor (advised));
        return enhancer.create ();
    }

    /**
     * 注意此处的MethodInterceptor是cglib中的接口，advised中的MethodInterceptor的AOP联盟中定义的接口，因此定义此类做适配
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;

        private DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            CglibMethodInvocation methodInvocation = new CglibMethodInvocation (advised.getTarget ().getTarget (), method, objects, methodProxy);
            if (advised.getMethodMatcher ().matches (method, advised.getTarget ().getTarget ().getClass ())) {
                //代理方法
                return advised.getMethodInterceptor ().invoke (methodInvocation);
            }
            return methodInvocation.proceed ();
        }
    }

    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
            super (method, arguments, target);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke (target, args);
        }
    }
}
