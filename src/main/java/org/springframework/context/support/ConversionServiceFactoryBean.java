package org.springframework.context.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Set;

/**
 * @date 2022/1/10 20:12
 */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {
    private DefaultConversionService service;
    private Set<?> converters;

    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    @Override
    public ConversionService getObject() throws Exception {
        return service;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service = new DefaultConversionService ();
        registerConversionService (converters, service);
    }

    private void registerConversionService(Set<?> converters, DefaultConversionService service) {
        for (Object converter : converters) {
            if (converter instanceof Converter) {
                service.addConverter ((Converter<?, ?>) converter);
            } else if (converter instanceof ConverterFactory) {
                service.addConverterFactory ((ConverterFactory<?, ?>) converter);
            } else if (converter instanceof GenericConverter) {
                service.addConverter ((GenericConverter) converter);
            } else throw new RuntimeException ();
        }
    }
}
