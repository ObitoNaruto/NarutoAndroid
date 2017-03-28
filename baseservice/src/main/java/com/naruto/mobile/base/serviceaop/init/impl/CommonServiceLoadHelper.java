package com.naruto.mobile.base.serviceaop.init.impl;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.init.BootLoader;
import com.naruto.mobile.base.serviceaop.service.ServicesLoader;

/**
 * 服务加载帮助类
 * Created by xinming.xxm on 2016/5/13.
 */
public class CommonServiceLoadHelper {

    private BootLoader mBootLoader;

    public CommonServiceLoadHelper(BootLoader bootLoader) {
        mBootLoader = bootLoader;
    }

    /**
     * 初始化加载服务
     */
    public void loadServices(){
        NarutoApplicationContext narutoApplicationContext = mBootLoader.getContext();
        NarutoApplication narutoApplication = narutoApplicationContext.getApplicationContext();


        try {
            initServices(narutoApplication);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 初始化基础服务
     * @param narutoApplication
     */
    private void initServices(NarutoApplication narutoApplication)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = narutoApplication.getClassLoader().loadClass("com.android.mobile.mywealth.framework.service.impl.ClientServicesLoader");
        ServicesLoader loader = (ServicesLoader) clazz.newInstance();
        loader.load();
    }
}
