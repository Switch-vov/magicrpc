package com.switchvov.magicrpc.core.api;

/**
 * 编解码
 *
 * @author switch
 * @since 2024/3/22
 */
public interface Codec {
    /**
     * 编码
     *
     * @param data
     * @return
     */
    byte[] encode(byte[] data);

    /**
     * 解码
     *
     * @param data
     * @return
     */
    byte[] decode(byte[] data);
}
