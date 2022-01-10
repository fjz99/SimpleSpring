package org.springframework.beans.factory.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.lang.reflect.Field;

/**
 * {@link Value}占位符解析的原理就是借助于{@link PropertyPlaceholderConfigurer}
 * 给bean factory添加一个，然后本class借助于这个resolver解析占位符
 * <p>
 * 在{@link ClassPathBeanDefinitionScanner#doScan(String...)}中配置了一个默认的@Value和@Autowired解析器
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private ConfigurableBeanFactory beanFactory;

    /**
     * set PropertyValue就行了，后面会属性注入的
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass ().getDeclaredFields ();
        for (Field field : fields) {
            //处理@Value
            Value valueAnnotation = field.getAnnotation (Value.class);
            if (valueAnnotation != null) {
                String value = beanFactory.resolveEmbeddedValue (valueAnnotation.value ());
                pvs.addPropertyValue (new PropertyValue (field.getName (), value));
            }

            //处理@Autowired
            Autowired autowired = field.getAnnotation (Autowired.class);
            if (autowired != null) {
                Qualifier qualifier = field.getAnnotation (Qualifier.class);
                Object value;
                //严格来说是不对的，其实@Qualifier的默认值是bean的名字，但是不一定是bean的名字
                //而且@Qualifier的值可以重复
                if (qualifier != null) {
                    value = beanFactory.getBean (qualifier.value (), field.getType ());
                } else {
                    value = beanFactory.getBean (field.getType ());
                }
                pvs.addPropertyValue (new PropertyValue (field.getName (), value));
            }
        }


        return pvs;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }
}
