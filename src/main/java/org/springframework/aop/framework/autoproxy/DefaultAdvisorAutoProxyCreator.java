package org.springframework.aop.framework.autoproxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.AdvisedSupport;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.*;

/**
 * 实现自动代理，基于BeanPostProcessor
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    /**
     * 为了避免重复创建代理(在解决循环依赖的时候)
     */
    private final Map<String, Object> createdProxies = new HashMap<> ();
    private DefaultListableBeanFactory beanFactory;
    private boolean proxyTargetClass = false;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    /**
     * 在init methods触发完之后再替换为代理对象
     * 支持多级代理,但是不支持通过Order指定顺序
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (createdProxies.containsKey (beanName)) {
            //直接在这里返回代理过的bean，解决代理的循环依赖的问题
            //这里我改动过了，保证没问题？？
            return createdProxies.remove (beanName);
        } else {
            return wrapIfNecessary (bean);
        }
    }

    private Object wrapIfNecessary(Object bean) {
        //避免死循环，即在创建advisors bean的时候也执行这个，然后就一直getBeansOfType
        if (isInfrastructureClass (bean.getClass ())) {
            return bean;
        }

        Collection<AspectJExpressionPointcutAdvisor> advisors =
                beanFactory.getBeansOfType (AspectJExpressionPointcutAdvisor.class).values ();
        Object res = bean;
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            if (advisor.getPointcut ().getClassFilter ().matches (bean.getClass ())) {
                TargetSource targetSource = new TargetSource (res);
                AdvisedSupport support = new AdvisedSupport (targetSource,
                        ((MethodInterceptor) advisor.getAdvice ()),
                        advisor.getPointcut ().getMethodMatcher ());
                support.setProxyTargetClass (proxyTargetClass);
                res = new ProxyFactory (support).getProxy ();
            }
        }
        return res;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom (beanClass)
                || Pointcut.class.isAssignableFrom (beanClass)
                || Advisor.class.isAssignableFrom (beanClass);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * 解决代理的循环依赖，提前暴露代理对象
     * <strong>注意：代理对象的循环依赖无法完全解决，见changelog.md<strong/>
     */
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        Object o = wrapIfNecessary (bean);
        createdProxies.put (beanName, o);
        return o;
    }

}
