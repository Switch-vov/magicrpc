package com.switchvov.magicrpc.core.util;

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
}
