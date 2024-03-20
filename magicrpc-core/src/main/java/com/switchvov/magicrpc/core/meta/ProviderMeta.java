package com.switchvov.magicrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * 描述Provider的映射关系
 *
 * @author switch
 * @since 2024/3/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProviderMeta {
    private Method method;
    private String methodSign;
    private Object serviceImpl;
}
