package com.naruto.mobile.base.serviceaop.service.ext;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.naruto.mobile.base.serviceaop.service.MicroService;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;

/**
 * Created by xinming.xxm on 2016/5/17.
 */
public class ExternalServiceManagerImpl extends ExternalServiceManager{

    /**
     * already regiested
     */
    private Map<String, ServiceDescription> regiestedExtServices = new ConcurrentHashMap<String, ServiceDescription>();
    /**
     * already created
     */
    private Map<String, ExternalService> createdExtServices = new ConcurrentHashMap<String, ExternalService>();

    @Override
    public void registerExtnernalService(ServiceDescription serviceDescription) {
        if(null == serviceDescription){
            return;
        }
        if(regiestedExtServices.containsKey(serviceDescription.getInterfaceClass())){
            return;
        }

        if(!serviceDescription.isLazy()){
            boolean result = createExternalService(serviceDescription);
            if(result){
                registerExternalServiceOnly(serviceDescription);
            }
        }
        else{
            registerExternalServiceOnly(serviceDescription);
        }

    }

    @Override
    public ExternalService getExternalService(String className) {
        ExternalService extService = createdExtServices.get(className);
        if (extService == null) {
            //外部服务(懒加载类型服务)都在MetaInfo中进行注册了,具体在BundleLoadHelper中进行的各个module中metaInfo的注册
            ServiceDescription description = regiestedExtServices.get(className);
            if (description == null) {
                return null;
            }
            synchronized (description) {
                try {
                    //这里的className就是具体实现的服务类名
                    Class<?> clazz = Class.forName(description.getClassName());
                    //对象初始化
                    extService = (ExternalService) clazz.newInstance();
                    //上下文注入
                    extService.attachContext(getNarutoApplicationContext());
                    //服务初始化，具体服务会回调onCreate方法
                    extService.create(null);
                } catch (Throwable e) {
                    return null;
                }
                //内存缓存起来
                createdExtServices.put(description.getInterfaceClass(), extService);
            }
        }
        return extService;
    }

    @Override
    public boolean createExternalService(ServiceDescription description) {
        if(null == description){
            return false;
        }
        if(createdExtServices.containsKey(description.getInterfaceClass())){
            return true;
        }

        synchronized (description){
            try{
                Class<?> clazz = Class.forName(description.getClassName());
                ExternalService extService = (ExternalService)clazz.newInstance();
                extService.attachContext(getNarutoApplicationContext());
                extService.create(null);
                createdExtServices.put(description.getInterfaceClass(), extService);
            }
            catch (Throwable e){
                return false;
            }
        }
        return true;
    }

    @Override
    public void registerExternalServiceOnly(ServiceDescription description) {
        if(null == description){
            return;
        }
        if(regiestedExtServices.containsKey(description.getInterfaceClass())){
            return;
        }
        regiestedExtServices.put(description.getInterfaceClass(), description);
    }

    @Override
    protected void onCreate(Bundle params) {

    }

    @Override
    protected void onDestroy(Bundle params) {

    }

    @Override
    public void saveState(SharedPreferences.Editor editor) {
        Set<String> classKeys = createdExtServices.keySet();

        String externalServicesMapStr = JSON.toJSONString(classKeys);
        editor.putString("_externalServiceClass_", externalServicesMapStr).commit();

        for (Object object : createdExtServices.values()) {
            if (object instanceof MicroService) {
                ((MicroService) object).saveState(editor);
            }
        }
    }

    @Override
    public void restoreState(SharedPreferences preferences) {
        String externalServiceClass = preferences.getString("_externalServiceClass_", null);
        if(externalServiceClass != null){
            Set<String> savededExternalServices = JSON.parseObject(externalServiceClass,new TypeReference<Set<String>>(){});
            if(savededExternalServices == null)
                return;

            Set<String> classNames = new HashSet<String>();
            for(String className :savededExternalServices){
                if(!createdExtServices.containsKey(className)){
                    classNames.add(className);
                }
            }

            for(String clazz:classNames){
                getExternalService(clazz);//lazy的service恢复到map中
            }
        }

        for (Object object : createdExtServices.values()) {
            if (object instanceof MicroService) {
                ((MicroService) object).restoreState(preferences);
            }
        }
    }
}
