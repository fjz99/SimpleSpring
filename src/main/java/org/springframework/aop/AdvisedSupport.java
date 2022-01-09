package org.springframework.aop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * 封装了被代理的类
 * 即包括目标对象、代理方法、切点（类已经固定，所以只需要{@link MethodMatcher}）
 * 相比Advisor，这个已经确定类了
 */
@Getter
@Setter
@NoArgsConstructor
public class AdvisedSupport {

    //是否使用cglib代理
    private boolean proxyTargetClass = false;

    private TargetSource target;
    private MethodInterceptor methodInterceptor;
    private MethodMatcher methodMatcher;

    public AdvisedSupport(TargetSource target, MethodInterceptor methodInterceptor, MethodMatcher methodMatcher) {
        this.target = target;
        this.methodInterceptor = methodInterceptor;
        this.methodMatcher = methodMatcher;
    }


}
