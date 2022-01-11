package org.springframework.core.convert.support;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 事实上,{@link GenericConverter}包含{@link ConverterFactory}包含{@link Converter}
 * 使用适配器将{@link ConverterFactory}和{@link Converter}统一转换成{@link GenericConverter}
 */
public class GenericConversionService implements ConversionService, ConverterRegistry {

    private final Map<ConvertiblePair, GenericConverter> converters = new HashMap<> ();

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return getConverter (sourceType, targetType) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(Object source, Class<T> targetType) {
        Class<?> sourceType = source.getClass ();
        GenericConverter converter = getConverter (sourceType, targetType);
        if (converter != null) {
            return (T) converter.convert (source, sourceType, targetType);
        } else throw new RuntimeException ();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addConverter(Converter<?, ?> converter) {
        ConvertiblePair pair = getRequiredTypeInfo (converter);
        converters.put (pair, new ConverterAdapter (pair, (Converter<Object, Object>) converter));
    }

    /**
     * 需要考虑父类问题
     */
    protected GenericConverter getConverter(Class<?> sourceType, Class<?> targetType) {
        //有序的，所以优先更精确的类型
        List<Class<?>> source = ClassUtils.getAllSuperclasses (sourceType);
        List<Class<?>> target = ClassUtils.getAllSuperclasses (targetType);
        source.add (sourceType);
        target.add (targetType);
        Collections.reverse (source);//翻转
        Collections.reverse (target);//翻转

        for (Class<?> a : source) {
            for (Class<?> b : target) {
                ConvertiblePair pair = new ConvertiblePair (a, b);
                if (converters.containsKey (pair)) {
                    return converters.get (pair);
                }
            }
        }
        return null;
    }

    /**
     * 我自己想的，不知道对不对
     * 有一个问题，就是无法确定factory支持的target类型
     * 只在map中保存父类型，然后查找的时候会用{@link #getConverter(Class, Class)}查找具体的类型
     */
    @Override
    @SuppressWarnings("unchecked")
    public void addConverterFactory(ConverterFactory<?, ?> converterFactory) {
        ConvertiblePair pair = getRequiredTypeInfo (converterFactory);
        converters.put (pair, new ConverterFactoryAdapter ((ConverterFactory<Object, Object>) converterFactory, pair));
    }

    private ConvertiblePair getRequiredTypeInfo(Object object) {
        Type[] types = object.getClass ().getGenericInterfaces ();
        ParameterizedType parameterized = (ParameterizedType) types[0];
        Type[] actualTypeArguments = parameterized.getActualTypeArguments ();
        Class<?> sourceType = (Class<?>) actualTypeArguments[0];
        Class<?> targetType = (Class<?>) actualTypeArguments[1];
        return new ConvertiblePair (sourceType, targetType);
    }

    @Override
    public void addConverter(GenericConverter converter) {
        for (ConvertiblePair type : converter.getConvertibleTypes ()) {
            converters.put (type, converter);
        }
    }

    /**
     * 适配器
     */
    private static final class ConverterAdapter implements GenericConverter {

        private final ConvertiblePair convertiblePair;
        private final Converter<Object, Object> converter;

        private ConverterAdapter(ConvertiblePair convertiblePair, Converter<Object, Object> converter) {
            this.convertiblePair = convertiblePair;
            this.converter = converter;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton (convertiblePair);
        }

        @Override
        public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {
            ConvertiblePair pair = new ConvertiblePair (sourceType, targetType);
            if (!pair.equals (convertiblePair)) {
                throw new IllegalArgumentException ();
            }

            return converter.convert (source);
        }
    }


    /**
     * 适配器
     */
    private static final class ConverterFactoryAdapter implements GenericConverter {
        private final ConverterFactory<Object, Object> converterFactory;
        private final ConvertiblePair convertiblePair;

        private ConverterFactoryAdapter(ConverterFactory<Object, Object> converterFactory, ConvertiblePair convertiblePair) {
            this.converterFactory = converterFactory;
            this.convertiblePair = convertiblePair;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton (convertiblePair);
        }


        @Override
        public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {
            if (!check (targetType, convertiblePair.getTargetType ())) {
                throw new RuntimeException ();
            }

            return converterFactory.getConverter (targetType).convert (source);
        }

        /**
         * 检查是不是子类
         */
        private boolean check(Class<?> childType, Class<?> superType) {
            while (childType != superType && childType != null) {
                childType = childType.getSuperclass ();
            }
            return childType != null;
        }
    }

}
