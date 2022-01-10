package org.springframework.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 记录一个kv
 */
@Data
@AllArgsConstructor
public class PropertyValue {

	private final String name;

	private Object value;

}
