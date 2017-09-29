package com.naruto.mobile.base.serviceaop.service.impl;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.demo.service.InnerTxtService;
import com.naruto.mobile.base.serviceaop.demo.service.TaskScheduleService;
import com.naruto.mobile.base.serviceaop.demo.service.impl.InnerTxtServiceImpl;
import com.naruto.mobile.base.serviceaop.demo.service.impl.TaskScheduleServiceImpl;
import com.naruto.mobile.base.serviceaop.service.MicroService;
import com.naruto.mobile.base.serviceaop.service.ServicesLoader;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class CommonServiceLoadAgent implements ServicesLoader {

    protected NarutoApplicationContext narutoApplicationContext;

    public CommonServiceLoadAgent() {
        narutoApplicationContext = NarutoApplication.getInstance().getNarutoApplicationContext();
    }

    public void preLoad() {
        // to do something before load service
    }

    public void postLoad() {
        //to do something after load service
    }

    @Override
    public final void load() {
        preLoad();

        //初始化各种服务
        registerService(InnerTxtService.class.getName(), new InnerTxtServiceImpl());
        registerService(TaskScheduleService.class.getName(), new TaskScheduleServiceImpl());
        //eg:
        //registerService(ExtTextService.class.getName(), new ExtTextServiceImpl());

        postLoad();
    }

    @Override
    public void afterBootLoad() {

    }

    //注入服务
    public final void registerService(String serviceName, MicroService service) {
        service.attachContext(narutoApplicationContext);
        narutoApplicationContext.registerService(serviceName, service);
    }

    //注入服务-懒加载
    public final void registerLazyService(String serviceName, String className) {
        narutoApplicationContext.registerService(serviceName, className);
    }
}
