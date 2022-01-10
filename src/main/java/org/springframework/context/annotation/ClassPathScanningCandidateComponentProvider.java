package org.springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 扫描{@link Component}
 */
public class ClassPathScanningCandidateComponentProvider {

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation (basePackage, Component.class);
        Set<BeanDefinition> result = new LinkedHashSet<> ();
        for (Class<?> aClass : classes) {
            BeanDefinition definition = new BeanDefinition (aClass);
            result.add (definition);
        }
        return result;
    }
}
