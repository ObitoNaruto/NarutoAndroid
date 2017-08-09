package com.naruto.mobile.h5container;

import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.h5container.service.H5Service;
import com.naruto.mobile.h5container.service.impl.H5ServiceImpl;

public class MetaInfo extends BaseMetaInfo {

    public MetaInfo(){
        super();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(H5Service.class.getSimpleName())
                .setClassName(H5ServiceImpl.class.getName());
        serviceDescription.setInterfaceClass(H5Service.class.getName());
        serviceDescription.setLazy(true);
        addService(serviceDescription);
    }


}
