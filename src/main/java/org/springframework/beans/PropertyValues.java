package org.springframework.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 记录每个bean的filed
 */
public class PropertyValues implements Iterable<PropertyValue> {

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

    @Override
    public Iterator<PropertyValue> iterator() {
        return new Iter();
    }

    private class Iter implements Iterator<PropertyValue> {
        private PropertyValue[] values;
        private int index = 0;

        private void set() {
            if (values == null) {
                values = getPropertyValues ();
            }
        }

        @Override
        public boolean hasNext() {
            set ();
            return index < values.length;
        }

        @Override
        public PropertyValue next() {
            set ();
            return values[index++];
        }
    }
}
