package com.switchvov.magicrpc.core.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.core.api.RpcContext;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.util.MethodUtils;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
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
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Class<?> service;
    private RpcContext context;
    private List<String> providers;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.methodSign(method));
        request.setArgs(args);

        List<String> urls = context.getRouter().route(providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        LOGGER.debug("loadBalancer.choose(urls) ==> {}", url);
        RpcResponse response = post(request, url);

        if (Objects.isNull(response)) {
            return null;
        }
        if (response.isStatus()) {
            String rspJson = objectMapper.writeValueAsString(response.getData());
            return objectMapper.readValue(rspJson, method.getReturnType());
        }
        throw new RuntimeException(response.getEx());
    }

    private RpcResponse post(RpcRequest request, String url) {
        String reqJson;
        try {
            reqJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Request req = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, MediaType.get(JSON_TYPE)))
                .build();
        try (Response rsp = client.newCall(req).execute()) {
            ResponseBody body = rsp.body();
            if (Objects.isNull(body)) {
                return null;
            }
            String rspJson = body.string();
            LOGGER.debug("req:{}, rsp:{}", reqJson, rspJson);
            if (!StringUtils.hasLength(rspJson)) {
                return null;
            }
            return objectMapper.readValue(rspJson, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
