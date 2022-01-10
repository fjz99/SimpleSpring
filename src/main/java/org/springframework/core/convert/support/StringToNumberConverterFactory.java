package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * {@link Number}是{@link Integer}等的公共抽象父类
 * 事实上,{@link GenericConverter}包含{@link ConverterFactory}包含{@link Converter}
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new ConverterImpl<> (targetType);
    }

    //注意泛型声明
    private static final class ConverterImpl<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        private ConverterImpl(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T convert(String source) {
            if (targetType == Integer.class) {
                return (T) Integer.valueOf (source);
            } else if (targetType == Double.class) {
                return (T) Double.valueOf (source);
            } else if (targetType == Long.class) {
                return (T) Long.valueOf (source);
            }
            throw new RuntimeException ();
        }

    }
}
