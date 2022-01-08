package org.springframework.beans.factory.support;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.Method;

/**
 * @date 2022/1/8 15:12
 */
public class DisposableBeanAdapter implements DisposableBean {
    private final String methodName;
    private final Object bean;

    public DisposableBeanAdapter(String beanName, String methodName, Object bean) {
        this.methodName = methodName;
        this.bean = bean;
    }

    @Override
    public void destroy() throws Exception {
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy ();
        }

        if (!StringUtils.isBlank (methodName) && !methodName.equals ("destroy")) {
            Method method = ClassUtils.getPublicMethod (bean.getClass (), methodName);
            if (method == null) {
                throw new BeansException ("");
            }
            method.invoke (bean);
        }
    }
}
