package com.naruto.mobile.base.serviceaop.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class BaseMetaInfo {

    public List<ServiceDescription> services = new ArrayList<ServiceDescription>();

    public List<ServiceDescription> getServices() {
        return services;
    }

    public void setServices(List<ServiceDescription> services) {
        this.services = services;
    }

    public void addService(ServiceDescription serviceDescription){
        if(null == services){
            services = new ArrayList<ServiceDescription>();
        }
        services.add(serviceDescription);
    }
}
