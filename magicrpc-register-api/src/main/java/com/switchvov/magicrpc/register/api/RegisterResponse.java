package com.switchvov.magicrpc.register.api;

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
public class RegisterResponse {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
}
