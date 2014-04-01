/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */

package org.comet4j.core.util;

// ~--- JDK imports ------------------------------------------------------------

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean反射工具
 */

public class BeanUtil {

	/** Creates a new instance of BeanUtil */
	private BeanUtil() {
	}

	@SuppressWarnings("rawtypes")
	public static Map getPropertiesByReflect(Object obj) throws Exception {
		if (obj == null) return null;
		Field[] fields = obj.getClass().getDeclaredFields();
		if (fields == null || fields.length == 0) return null;
		Map<String, Object> map = new HashMap<String, Object>();
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			map.put(field.getName(), field.get(obj));

		}
		if (map.size() < 1) return null;
		return map;
	}

}
