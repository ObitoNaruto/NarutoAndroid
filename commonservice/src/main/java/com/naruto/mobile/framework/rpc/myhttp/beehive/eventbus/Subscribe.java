package com.naruto.mobile.framework.rpc.myhttp.beehive.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册 事件监听注解
 * 用法：
 *
 * @Subscribe
 * public void onEvent(SomeObject obj)
 *
 * @Subscribe(name="some_event_name")
 * public void onEvent()
 *
 * Created by aowen on 15/7/6.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    /**
     * 指定事件名称，如未设置则使用目标方法的参数(必须有1个参数)类型作为事件
     */
    String name() default "";

    /**
     * 指定事件handler执行在哪个线程，如未设置则默认使用当前线程
     */
    String threadMode() default "current";

    /**
     * 如果指定事件存在白名单限制，则使用此白名单串表示是否可有效注册
     * @return
     */
    String whiteListKey() default "";

}


