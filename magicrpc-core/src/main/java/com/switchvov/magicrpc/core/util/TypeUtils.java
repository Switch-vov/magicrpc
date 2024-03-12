package com.switchvov.magicrpc.core.util;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/13
 */
public class TypeUtils {
    private static final String FIELD_TYPE = "TYPE";
    private static final List<Class<?>> PRIMITIVE_TYPES = List.of(
            Integer.class,
            Long.class,
            Short.class,
            Byte.class,
            Float.class,
            Double.class,
            Character.class,
            Boolean.class
    );

    public static Class<?> primitiveTypeForName(String name) throws NoSuchFieldException, IllegalAccessException {
        for (Class<?> type : PRIMITIVE_TYPES) {
            Class<?> primitiveClass = (Class<?>) type.getField(FIELD_TYPE).get(null);
            if (primitiveClass.getCanonicalName().equals(name)) {
                return primitiveClass;
            }
        }
        throw new RuntimeException("not found primitive type for name: " + name);
    }
}
