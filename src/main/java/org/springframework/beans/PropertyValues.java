package org.springframework.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录每个bean的filed
 */
public class PropertyValues {

    private final Map<String, PropertyValue> propertyValueMap = new HashMap<> ();

    public void addPropertyValue(PropertyValue pv) {
        propertyValueMap.put (pv.getName (), pv);
    }

    public PropertyValue[] getPropertyValues() {
        return propertyValueMap.values ().toArray (new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        return propertyValueMap.get (propertyName);
    }
}
