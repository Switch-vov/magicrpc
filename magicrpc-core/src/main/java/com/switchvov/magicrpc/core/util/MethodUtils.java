package com.switchvov.magicrpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author switch
 * @since 2024/3/10
 */
public class MethodUtils {
    public static boolean checkLocalMethod(final String method) {
        if ("toString".equals(method) ||
                "hashCode".equals(method) ||
                "notifyAll".equals(method) ||
                "equals".equals(method) ||
                "wait".equals(method) ||
                "getClass".equals(method) ||
                "notify".equals(method)) {
            return true;
        }
        return false;
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
}
