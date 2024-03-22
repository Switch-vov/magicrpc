package com.switchvov.magicrpc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author switch
 * @since 2024/3/22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableProvider {
    /**
     * 扫描的包路径
     *
     * @return
     */
    String[] scanBasePackages() default {};
}
