package org.springframework.aop.aspectj;


import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;

/**
 * Advisor，封装了advice、pointcut
 * 即，相对于{@link org.springframework.aop.AdvisedSupport}
 * 这个类是针对于所有类的，也存在类匹配的问题
 */
@NoArgsConstructor
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    private Advice advice;
    private String expression;
    private Pointcut pointcut;

    public AspectJExpressionPointcutAdvisor(Advice advice, String expression) {
        this.advice = advice;
        this.expression = expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        if (pointcut == null) {
            pointcut = new AspectJExpressionPointcut (expression);
        }
        return pointcut;
    }
}
