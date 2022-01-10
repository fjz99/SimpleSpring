package org.springframework.core.convert.converter;

import lombok.EqualsAndHashCode;

import java.util.Set;


public interface GenericConverter {

	Set<ConvertiblePair> getConvertibleTypes();

	Object convert(Object source, Class<?> sourceType, Class<?> targetType);

	@EqualsAndHashCode
	final class ConvertiblePair {

		private final Class<?> sourceType;

		private final Class<?> targetType;

		public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		public Class<?> getSourceType() {
			return this.sourceType;
		}

		public Class<?> getTargetType() {
			return this.targetType;
		}

	}

}
