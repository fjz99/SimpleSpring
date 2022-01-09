package org.springframework.aop.framework;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * 封装了方法的执行过程
 */
public class ReflectiveMethodInvocation implements MethodInvocation {
    protected final Method method;
    protected final Object[] args;
    protected final Object target;

    public ReflectiveMethodInvocation(Method method, Object[] args, Object target) {
        this.method = method;
        this.args = args;
        this.target = target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public Object proceed() throws Throwable {
        return method.invoke (target, args);
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }
}
