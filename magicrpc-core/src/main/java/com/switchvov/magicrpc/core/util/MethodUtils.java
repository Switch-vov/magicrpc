package com.switchvov.magicrpc.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author switch
 * @since 2024/3/10
 */
public class MethodUtils {
    public static boolean checkLocalMethod(final String method) {
        return "toString".equals(method) ||
                "hashCode".equals(method) ||
                "notifyAll".equals(method) ||
                "equals".equals(method) ||
                "wait".equals(method) ||
                "getClass".equals(method) ||
                "notify".equals(method);
    }

    public static boolean checkLocalMethod(final Method method) {
        return checkLocalMethod(method.getName());
    }

    public static String methodSign(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(
                c -> sb.append("_").append(c.getCanonicalName())
        );
        return sb.toString();
    }

    public static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<>();
        while (aClass != null) {
            fields.addAll(Arrays.stream(aClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(annotationClass))
                    .toList());
            aClass = aClass.getSuperclass();
        }
        return fields;
    }


}
