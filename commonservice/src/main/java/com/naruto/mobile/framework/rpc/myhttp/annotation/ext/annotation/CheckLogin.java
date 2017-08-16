package com.naruto.mobile.framework.rpc.myhttp.annotation.ext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 检查登录
 * 
 * @author sanping.li@alipay.com
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckLogin {
    public boolean allowBack() default true;
}
