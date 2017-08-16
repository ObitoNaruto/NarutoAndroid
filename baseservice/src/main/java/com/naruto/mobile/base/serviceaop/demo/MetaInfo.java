package com.naruto.mobile.base.serviceaop.demo;


import com.naruto.mobile.base.serviceaop.broadcast.BroadcastReceiverDescription;
import com.naruto.mobile.base.serviceaop.demo.broadcast.TestDemoBroadcastReceiver;
import com.naruto.mobile.base.serviceaop.demo.broadcast.TestDemoReceiverConstants;
import com.naruto.mobile.base.serviceaop.demo.service.ExtTextService;
import com.naruto.mobile.base.serviceaop.demo.service.impl.ExtTextServiceImpl;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class MetaInfo extends BaseMetaInfo {

    public MetaInfo() {
        super();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName("ExtTextService").
                setClassName(ExtTextServiceImpl.class.getName());
        serviceDescription.setInterfaceClass(ExtTextService.class.getName());
        serviceDescription.setLazy(true);
        addService(serviceDescription);


        BroadcastReceiverDescription broadcastReceiverDescription = new BroadcastReceiverDescription();
        broadcastReceiverDescription.setClassName(TestDemoBroadcastReceiver.class.getName());
        broadcastReceiverDescription.setName(TestDemoBroadcastReceiver.class.getSimpleName());
        broadcastReceiverDescription.setMsgCode(new String[]{TestDemoReceiverConstants.TEST_BROADCAST_RECEIVER});
        addBroadcastReceiver(broadcastReceiverDescription);

    }
}
