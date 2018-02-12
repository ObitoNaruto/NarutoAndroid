package com.naruto.mobile.base.serviceaop.service.impl;

import android.content.SharedPreferences;
import android.text.TextUtils;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.service.CommonService;
import com.naruto.mobile.base.serviceaop.service.MicroService;
import com.naruto.mobile.base.serviceaop.service.ServiceManager;

/**
 * Created by xinming.xxm on 2016/5/13.
 */
public class ServiceManagerImpl implements ServiceManager {

    //当前项目上下文
    private NarutoApplicationContext mNarutoApplication;

    /**
     * 服务
     */
    private Map<String, Object> mServices;//及时加载的服务map
    private Map<String, String> mLazyServices;//懒加载的服务map

    /**
     * 初始化服务容器（内存缓存）
     */
    public ServiceManagerImpl(){
        //初始化，线程安全
        mServices = new ConcurrentHashMap<>();
        mLazyServices = new ConcurrentHashMap<>();
    }

    @Override
    public void attachContext(NarutoApplicationContext applicationContext) {
        //附着app上下文
        mNarutoApplication = applicationContext;
    }

    @Override
    public <T> boolean registerService(String className, T service) {
        //已被实例化的服务
        if (service instanceof MicroService) {
            return mServices.put(className, service) != null;
        } else if (service instanceof String) {
            //还未被实例化的服务
            return mLazyServices.put(className, (String) service) != null;
        } else { // 未知类型
            return mServices.put(className, service) != null;
        }
    }

    @Override
    public <T> T findServiceByInterface(String className) {
        //内存缓存中已经包含待查找的服务，取出直接返回
        if (mServices.containsKey(className)) {
            return (T) mServices.get(className);
        } else if (mLazyServices.containsKey(className)) {//懒加载的服务
            String serviceClassName = mLazyServices.get(className);
            if (TextUtils.isEmpty(serviceClassName)) {
                return null;
            }
            synchronized (serviceClassName) {
                // check service again for synchronized
                if (mServices.containsKey(className)) {
                    return (T) mServices.get(className);
                }
                //约定好，外部扩展服务都是CommonService类型服务,内部服务都已经初始化了且存在于内存了
                CommonService service = null;
                try {
                    //通过当前上下文的classLoader获取Class对象
                    Class<?> clazz = mNarutoApplication
                            .getApplicationContext()
                            .getClassLoader()
                            .loadClass(serviceClassName);
                    //通过反射进行服务的初始化
                    service = (CommonService) clazz.newInstance();
                } catch (ClassNotFoundException e) {
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
                if (service != null) {
                    //为服务注入项目上下文环境
                    service.attachContext(mNarutoApplication);
                    //将初始化后的服务内存缓存起来
                    mServices.put(className, service);
                }
                return (T) service;
            }
        }
        return null;
    }

    @Override
    public void onDestroyService(MicroService microService) {
        if (null == microService) {
            return;
        }
        Set<String> keys = mServices.keySet();
        MicroService service = null;
        Object obj = null;
        for(String key : keys) {
            obj = mServices.get(key);
            if (obj instanceof MicroService) {
                service = (MicroService) obj;
                if (service == microService) {
                    mServices.remove(key);
                    break;
                }
            }
        }
    }

    @Override
    public void exit() {
        Object[] values = mServices.values().toArray();
        MicroService service = null;
        for (Object object : values) {
            if (object instanceof MicroService) {
                service = (MicroService) object;
                if (service.isActivated()) {
                    service.destroy(null);
                }
            }
        }
        mServices.clear();
        mLazyServices.clear();
    }

    @Override
    public void saveState(SharedPreferences.Editor editor) {
        for (Object object : mServices.values()) {
            if (object instanceof MicroService) {
                ((MicroService) object).saveState(editor);
            }
        }
    }

    @Override
    public void restoreState(SharedPreferences preferences) {
        for (Object object : mServices.values()) {
            if (object instanceof MicroService) {
                ((MicroService) object).restoreState(preferences);
            }
        }
    }

    @Override
    public <T> T unregisterService(String interfaceName) {
        mLazyServices.remove(interfaceName);
        return (T)mServices.remove(interfaceName);
    }
}
