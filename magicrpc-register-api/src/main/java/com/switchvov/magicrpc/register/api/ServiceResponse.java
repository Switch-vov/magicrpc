package com.switchvov.magicrpc.register.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("hosts")
    private List<String> hosts;
}
