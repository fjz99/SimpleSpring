package org.springframework.aop;

import lombok.Getter;

/**
 * 是目标对象的封装
 */
@Getter
public class TargetSource {
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    public Class<?>[] getTargetInterfaces(){
        return target.getClass ().getInterfaces ();
    }

}
