package com.switchvov.magicrpc.core.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author switch
 * @since 2024/3/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MagicInvocationHandler implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MagicInvocationHandler.class);
    public static final String JSON_TYPE = "application/json;charset=utf-8";
    private Class<?> service;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethod(method.getName());
        request.setArgs(args);

        RpcResponse response = post(request);
        if (Objects.isNull(response)) {
            throw new RuntimeException("response is null");
        }
        if (response.isStatus()) {
            String rspJson = objectMapper.writeValueAsString(response.getData());
            return objectMapper.readValue(rspJson, method.getReturnType());
        }
        throw new RuntimeException(response.getEx());
    }

    private RpcResponse post(RpcRequest request) {
        String reqJson;
        try {
            reqJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Request req = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, MediaType.get(JSON_TYPE)))
                .build();
        try (Response rsp = client.newCall(req).execute()) {
            ResponseBody body = rsp.body();
            if (Objects.isNull(body)) {
                return null;
            }
            String rspJson = body.string();
            LOGGER.debug("req:{}, rsp:{}", reqJson, rspJson);
            return objectMapper.readValue(rspJson, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}