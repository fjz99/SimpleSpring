package org.springframework.beans.factory.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.PropertyValues;

@Setter
@Getter
public class BeanDefinition {

    public static final String SCOPE_SINGLETON = "singleton";

    public static final String SCOPE_PROTOTYPE = "prototype";

    private Class<?> beanClass;

    private PropertyValues propertyValues;

    private String initMethodName;

    private String destroyMethodName;

    private String scope = SCOPE_SINGLETON;

    @Setter(AccessLevel.NONE)
    private boolean singleton = true;

    @Setter(AccessLevel.NONE)
    private boolean prototype = false;

    public BeanDefinition(Class<?> beanClass) {
        this (beanClass, null);
    }

    public BeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues ();
    }

    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals (scope);
        this.prototype = SCOPE_PROTOTYPE.equals (scope);
    }
}
