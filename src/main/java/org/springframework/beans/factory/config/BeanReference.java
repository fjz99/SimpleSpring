package org.springframework.beans.factory.config;

import lombok.Data;

/**
 * 封装bean之间的引用
 */
@Data
public class BeanReference {
    private final String beanName;
}
