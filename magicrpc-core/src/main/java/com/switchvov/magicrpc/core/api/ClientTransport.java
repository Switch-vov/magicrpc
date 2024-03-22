package com.switchvov.magicrpc.core.api;

/**
 * 客户端Transport
 *
 * @author switch
 * @since 2024/3/22
 */
public interface ClientTransport {

    /**
     * 访问服务端
     *
     * @param req
     * @return
     */
    byte[] roundTrip(byte[] req);
}
