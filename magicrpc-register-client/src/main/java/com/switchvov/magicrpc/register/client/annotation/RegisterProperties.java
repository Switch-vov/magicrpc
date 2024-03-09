package com.switchvov.magicrpc.register.client.annotation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author switch
 * @since 2024/3/9
 */
@ConfigurationProperties(RegisterProperties.PREFIX)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterProperties {
    /**
     * Configuration prefix.
     */
    public static final String PREFIX = "magicrpc.register";
    /**
     * Connection string to the register.
     */
    private String connectString = "http://localhost:8082";
    private String registerPath = "/api/service";
}