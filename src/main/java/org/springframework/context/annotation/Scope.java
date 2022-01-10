package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    String SINGLETON = BeanDefinition.SCOPE_SINGLETON;
    String PROTOTYPE = BeanDefinition.SCOPE_PROTOTYPE;

    String value() default SINGLETON;

}
