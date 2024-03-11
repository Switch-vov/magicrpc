package com.switchvov.magicrpc.register.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.register.api.RegisterRequest;
import com.switchvov.magicrpc.register.api.RegisterResponse;
import com.switchvov.magicrpc.register.api.ServiceResponse;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author switch
 * @since 2024/3/9
 */
@RestController
public class RegisterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    private static final String SEP = "/";
    private static final String SERVICE_PREFIX = "/magicrpc/register/service/";

    private final CuratorFramework curatorFramework;
    private final ObjectMapper objectMapper;

    public RegisterController(CuratorFramework curatorFramework, ObjectMapper objectMapper) {
        this.curatorFramework = curatorFramework;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/api/service")
    public RegisterResponse registerService(@RequestBody RegisterRequest request) {
        String service = SERVICE_PREFIX + request.getServiceName();
        String instance = service + SEP + request.getHost();
        try {
            String content = objectMapper.writeValueAsString(request);
            LOGGER.info("register service:{}, instance:{} content:{}",
                    service, request.getHost(), content);
            if (Objects.isNull(curatorFramework.checkExists().forPath(service))) {
                curatorFramework.create()
                        .creatingParentContainersIfNeeded()
                        .withMode(CreateMode.CONTAINER)
                        .forPath(service);
            }
            if (Objects.isNull(curatorFramework.checkExists().forPath(instance))) {
                curatorFramework.create()
                        .creatingParentContainersIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(instance, content.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            LOGGER.error("register service:{}, instance:{} error", service, request.getHost(), e);
            return new RegisterResponse(500, "register error");
        }
        return new RegisterResponse(0, "");
    }

    @GetMapping("/api/service/{service}")
    public ServiceResponse getService(@PathVariable(name = "service") String service) {
        String serviceName = SERVICE_PREFIX + service;
        ServiceResponse response = new ServiceResponse(0, "", new ArrayList<>());
        try {
            if (Objects.isNull(curatorFramework.checkExists().forPath(serviceName))) {
                return response;
            }
            List<String> hosts = curatorFramework.getChildren().forPath(serviceName);
            response.setHosts(hosts);
        } catch (Exception e) {
            LOGGER.error("get service:{} error", serviceName, e);
            return new ServiceResponse(500, "get service error", new ArrayList<>());
        }
        return response;
    }
}
