package com.switchvov.magicrpc.register.client.annotation;

import com.switchvov.magicrpc.register.api.RegisterRequest;
import com.switchvov.magicrpc.register.api.RegisterResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.lang.annotation.Annotation;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 注册客户端
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterClientHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegisterClientHandler.class);
    private RegisterProperties registerProperties;
    private ApplicationContext applicationContext;
    private RestClient restClient;

    protected void register() {
        // get ip&port of local server
        String ip;
        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> registerClients = applicationContext.getBeansWithAnnotation(RegisterClient.class);
        int port = ((AnnotationConfigServletWebServerApplicationContext) applicationContext).getWebServer().getPort();


        for (String registerName : registerClients.keySet()) {
            RegisterClient client = applicationContext.findAnnotationOnBean(registerName, RegisterClient.class);
            if (client == null) {
                continue;
            }
            Class<? extends Annotation>[] registerAnnotations = client.registerAnnotationsOnClass();
            for (Class<? extends Annotation> annotation : registerAnnotations) {
                Map<String, Object> registers = applicationContext.getBeansWithAnnotation(annotation);
                registers.values().forEach(x -> {
                    Class<?> service = x.getClass().getInterfaces()[0];
                    registerService(service.getCanonicalName(), ip, port);
                });
            }
        }
    }

    private void registerService(String service, String address, Integer port) {
        RegisterRequest request = new RegisterRequest(service, address, port);
        String registerUrl = registerProperties.getConnectString() + registerProperties.getRegisterPath();
        ResponseEntity<RegisterResponse> response = restClient.post()
                .uri(registerUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RegisterResponse.class);
        LOGGER.info("register service:{}, host:{}, response:{}", service, request.getHost(), response);
    }
}
