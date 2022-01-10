package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实现占位符替换
 * 即类似于@Value
 * 这个类需要被配置为bean
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";
    private static final Pattern MATCH = Pattern.compile ("\\$\\{(.*)}");
    private String location;

    protected Properties loadProperty() throws IOException {
        ResourceLoader loader = new DefaultResourceLoader ();
        Resource resource = loader.getResource (location);
        Properties properties = new Properties ();
        properties.load (resource.getInputStream ());
        return properties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Properties properties;
        try {
            properties = loadProperty ();
        } catch (IOException e) {
            e.printStackTrace ();
            throw new BeansException ("", e);
        }

        for (String name : beanFactory.getBeanDefinitionNames ()) {
            BeanDefinition definition = beanFactory.getBeanDefinition (name);
            for (PropertyValue propertyValue : definition.getPropertyValues ().getPropertyValues ()) {
                resolvePlaceHolder (propertyValue, properties);
            }
        }
    }

    protected void resolvePlaceHolder(PropertyValue propertyValue, Properties properties) {
        if (propertyValue.getValue () instanceof String) {
            String value = (String) propertyValue.getValue ();

            Matcher matcher = MATCH.matcher (value);
            if (matcher.matches ()) {
                String group = matcher.group (1);
                String property = properties.getProperty (group);
                if (property != null) {
                    propertyValue.setValue (property);
                } else throw new BeansException ();
            }
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
