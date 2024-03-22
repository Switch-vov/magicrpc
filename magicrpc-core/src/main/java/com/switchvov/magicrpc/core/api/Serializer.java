package com.switchvov.magicrpc.core.api;

/**
 * 序列化反序列化接口
 *
 * @author switch
 * @since 2024/3/21
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param data
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T data);

    /**
     * 反序列化
     *
     * @param data
     * @param type
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> type);
}
