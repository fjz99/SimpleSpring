package org.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @date 2022/1/10 12:31
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";

    private final BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            Set<BeanDefinition> components = findCandidateComponents (basePackage);
            for (BeanDefinition component : components) {
                setScope (component);
                registry.registerBeanDefinition (resolveBeanName (component), component);
            }
        }

        //！！配置一个默认的@Value和@Autowired解析器
        registry.registerBeanDefinition (AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME,
                new BeanDefinition (AutowiredAnnotationBeanPostProcessor.class));
    }

    protected String resolveBeanName(BeanDefinition component) {
        Class<?> beanClass = component.getBeanClass ();
        String value = beanClass.getAnnotation (Component.class).value ();
        if (StringUtils.isNotBlank (value)) {
            return value;
        } else {
            return StrUtil.lowerFirst (beanClass.getSimpleName ());
        }
    }

    protected void setScope(BeanDefinition definition) {
        Class<?> beanClass = definition.getBeanClass ();
        Scope annotation = beanClass.getAnnotation (Scope.class);
        boolean isSingleton = true;
        if (annotation != null) {
            if (!annotation.value ().equals (Scope.PROTOTYPE) &&
                    !annotation.value ().equals (Scope.SINGLETON)) {
                throw new BeansException ();
            }

            if (annotation.value ().equals (Scope.PROTOTYPE)) {
                isSingleton = false;
            }
        }

        if (isSingleton) {
            definition.setScope (BeanDefinition.SCOPE_SINGLETON);
        } else {
            definition.setScope (BeanDefinition.SCOPE_PROTOTYPE);
        }
    }

}
