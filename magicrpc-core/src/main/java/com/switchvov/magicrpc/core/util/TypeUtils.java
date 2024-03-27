package com.switchvov.magicrpc.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.core.api.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author switch
 * @since 2024/3/13
 */
@Slf4j
public class TypeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TypeUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Object cast(Object origin, Class<?> type) {
        if (Objects.isNull(origin)) {
            return null;
        }
        Class<?> aClass = origin.getClass();
        if (type.isAssignableFrom(aClass)) {
            return origin;
        }

        if (type.isArray()) {
            if (origin instanceof List<?> list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                    Array.set(resultArray, i, Array.get(origin, i));
                } else {
                    Object castObject = cast(Array.get(origin, i), componentType);
                    Array.set(resultArray, i, castObject);
                }
            }
            return resultArray;
        }

        if (origin instanceof HashMap<?, ?> map) {
            return OBJECT_MAPPER.convertValue(map, type);
        }

        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.valueOf(origin.toString());
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(origin.toString());
        } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(origin.toString());
        } else if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(origin.toString());
        } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return origin.toString().charAt(0);
        } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return Boolean.valueOf(origin.toString());
        } else if (type.equals(String.class)) {
            return origin.toString();
        }
        return null;
    }

    public static Object castMethodResult(Method method, Object data) {
        try {
            String rspJson = OBJECT_MAPPER.writeValueAsString(data);
            return OBJECT_MAPPER.readValue(rspJson, method.getReturnType());
        } catch (JsonProcessingException e) {
            throw new RpcException(e);
        }
    }

    // TODO: 调成下面这种手动cast解决方案
//    public static Object castMethodResultBasic(Method method, Object data) {
//        Class<?> type = method.getReturnType();
//        log.debug("method.getReturnType() = {}", type);
//        if (data instanceof JSONObject jsonResult) {
//            if (Map.class.isAssignableFrom(type)) {
//                Map resultMap = new HashMap();
//                Type genericReturnType = method.getGenericReturnType();
//                log.debug("return type:{}", genericReturnType);
//                if (genericReturnType instanceof ParameterizedType parameterizedType) {
//                    Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
//                    Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
//                    log.debug("keyType  : {}", keyType);
//                    log.debug("valueType: {}", valueType);
//                    jsonResult.entrySet().stream().forEach(
//                            e -> {
//                                Object key = cast(e.getKey(), keyType);
//                                Object value = cast(e.getValue(), valueType);
//                                resultMap.put(key, value);
//                            }
//                    );
//                }
//                return resultMap;
//            }
//            return jsonResult.toJavaObject(type);
//        } else if (data instanceof JSONArray jsonArray) {
//            Object[] array = jsonArray.toArray();
//            if (type.isArray()) {
//                Class<?> componentType = type.getComponentType();
//                Object resultArray = Array.newInstance(componentType, array.length);
//                for (int i = 0; i < array.length; i++) {
//                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
//                        Array.set(resultArray, i, array[i]);
//                    } else {
//                        Object castObject = cast(array[i], componentType);
//                        Array.set(resultArray, i, castObject);
//                    }
//                }
//                return resultArray;
//            } else if (List.class.isAssignableFrom(type)) {
//                List<Object> resultList = new ArrayList<>(array.length);
//                Type genericReturnType = method.getGenericReturnType();
//                log.debug("return type:{}", genericReturnType);
//                if (genericReturnType instanceof ParameterizedType parameterizedType) {
//                    Type actualType = parameterizedType.getActualTypeArguments()[0];
//                    log.debug("actual type:{}", actualType);
//                    for (Object o : array) {
//                        resultList.add(cast(o, (Class<?>) actualType));
//                    }
//                } else {
//                    resultList.addAll(Arrays.asList(array));
//                }
//                return resultList;
//            } else {
//                return null;
//            }
//        } else {
//            return cast(data, type);
//        }
//    }
}
