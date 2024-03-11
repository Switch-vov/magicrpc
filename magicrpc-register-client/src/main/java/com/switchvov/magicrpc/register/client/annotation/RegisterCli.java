package com.switchvov.magicrpc.register.client.annotation;

import com.switchvov.magicrpc.register.api.ServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author switch
 * @since 2024/3/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCli {
    private RegisterProperties registerProperties;
    private RestClient restClient;

    public List<String> getHosts(String serviceName) {
        String registerUrl = registerProperties.getConnectString() + registerProperties.getRegisterPath();
        registerUrl += "/" + serviceName;
        ResponseEntity<ServiceResponse> response = restClient.get()
                .uri(registerUrl)
                .retrieve()
                .toEntity(ServiceResponse.class);
        if (Objects.isNull(response.getBody())) {
            return new ArrayList<>();
        }
        return response.getBody().getHosts();
    }
}
