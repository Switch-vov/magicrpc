package com.switchvov.magicrpc.core.consumer.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.consumer.HttpInvoker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author switch
 * @since 2024/3/20
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {
    private static final String JSON_TYPE = "application/json;charset=utf-8";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OkHttpInvoker() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson;
        try {
            reqJson = objectMapper.writeValueAsString(rpcRequest);
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
            log.debug("req:{}, rsp:{}", reqJson, rspJson);
            if (!StringUtils.hasLength(rspJson)) {
                return null;
            }
            return objectMapper.readValue(rspJson, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
