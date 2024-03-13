package com.switchvov.magicrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author switch
 * @since 2024/3/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderMeta {
    private Method method;
    private String methodSign;
    private Object serviceImpl;
}
