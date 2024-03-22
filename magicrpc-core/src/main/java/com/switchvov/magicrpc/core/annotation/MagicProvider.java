package com.switchvov.magicrpc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO:
 * <p>
 * 1、写一个xxrpc的starter
 * <p>
 * 2、将Provider注解依赖于spring的bean注解去掉，只用Provider就实现功能。
 *
 * @author switch
 * @since 2024/3/6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface MagicProvider {

}
