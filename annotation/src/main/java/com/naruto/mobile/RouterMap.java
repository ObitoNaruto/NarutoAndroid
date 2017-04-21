package com.naruto.mobile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 编译时期的注解，主要作用于Class。之后，在调用的地方就是需要使用我们的这个注解。
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RouterMap {
    String [] value();
}
