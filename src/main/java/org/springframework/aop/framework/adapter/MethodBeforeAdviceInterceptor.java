package org.springframework.aop.framework.adapter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * 适配器，实现前置通知
 * 因为总体都是执行{@link org.springframework.aop.AdvisedSupport}中的
 * {@link org.aopalliance.intercept.MethodInterceptor}都是环绕通知。
 * 这个类将环绕通知变成前置通知
 */
@NoArgsConstructor
@Setter
@Getter
public class MethodBeforeAdviceInterceptor implements MethodInterceptor {
    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        advice.before (invocation.getMethod (), invocation.getArguments (), invocation.getThis ());
        return invocation.proceed ();
    }
}
