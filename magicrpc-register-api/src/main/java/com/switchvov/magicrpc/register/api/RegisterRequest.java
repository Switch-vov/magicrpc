package com.switchvov.magicrpc.register.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author switch
 * @since 2024/3/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @JsonProperty("service_name")
    private String serviceName;
    @JsonProperty("address")
    private String address;
    @JsonProperty("port")
    private Integer port;

    @JsonIgnore
    public String getHost() {
        return address + ":" + port;
    }
}
