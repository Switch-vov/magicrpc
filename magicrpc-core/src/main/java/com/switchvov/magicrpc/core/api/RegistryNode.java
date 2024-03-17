package com.switchvov.magicrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author switch
 * @since 2024/3/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistryNode {
    private String serverUrl;
    private int weight;
}
