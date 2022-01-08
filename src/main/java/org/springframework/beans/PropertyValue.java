package org.springframework.beans;

import lombok.Data;

/**
 * 记录一个kv
 */
@Data
public class PropertyValue {

	private final String name;

	private final Object value;

}
