package com.switchvov.magicrpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author switch
 * @since 2024/3/10
 */
public class MethodUtils {
    public static final String METHOD_SEPARATOR = "@";
    public static final String PARAM_SEPARATOR = "&";

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

    public static String getMethodNameBySign(String methodSign) {
        int index = methodSign.indexOf(METHOD_SEPARATOR);
        if (index < 0) {
            return methodSign;
        }
        return methodSign.substring(0, index);
    }

    public static String[] getMethodParamTypesBySign(String methodSign) {
        int index = methodSign.indexOf(METHOD_SEPARATOR);
        if (index < 0) {
            return new String[]{};
        }
        return Optional.ofNullable(methodSign.substring(index + 1).split(PARAM_SEPARATOR)).orElse(new String[]{});
    }

    public static String generateMethodSign(Method method) {
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            return methodName;
        }
        String params = Arrays.stream(paramTypes)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(PARAM_SEPARATOR));
        return methodName + METHOD_SEPARATOR + params;
    }
}
