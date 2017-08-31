package com.naruto.mobile.base.serviceaop.demo;


import com.naruto.mobile.base.serviceaop.broadcast.BroadcastReceiverDescription;
import com.naruto.mobile.base.serviceaop.demo.broadcast.TestDemoBroadcastReceiver;
import com.naruto.mobile.base.serviceaop.demo.service.ExtTextService;
import com.naruto.mobile.base.serviceaop.demo.service.impl.ExtTextServiceImpl;
import com.naruto.mobile.base.serviceaop.demo.task.CommonServiceValue;
import com.naruto.mobile.base.serviceaop.demo.task.TestDemoTask;
import com.naruto.mobile.base.serviceaop.demo.task.TestDemoTask2;
import com.naruto.mobile.base.serviceaop.demo.task.TestDemoTask3;
import com.naruto.mobile.base.serviceaop.msg.MsgCodeConstants;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.base.serviceaop.task.ValueDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class MetaInfo extends BaseMetaInfo {

    public MetaInfo() {
        super();

        //初始化服务ExtTextService
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName("ExtTextService").
                setClassName(ExtTextServiceImpl.class.getName());
        serviceDescription.setInterfaceClass(ExtTextService.class.getName());
        serviceDescription.setLazy(true);
        addService(serviceDescription);

        //框架初始化广播TestDemoBroadcastReceiver
        BroadcastReceiverDescription broadcastReceiverDescription = new BroadcastReceiverDescription();
        broadcastReceiverDescription.setClassName(TestDemoBroadcastReceiver.class.getName());
        broadcastReceiverDescription.setName(TestDemoBroadcastReceiver.class.getSimpleName());
        broadcastReceiverDescription.setMsgCode(new String[]{MsgCodeConstants.FRAMEWORK_INITED});
        addBroadcastReceiver(broadcastReceiverDescription);

        //初始化管道任务，管道任务起始task
        ValueDescription valueDescription = new ValueDescription();
        valueDescription.setClassName(CommonServiceValue.class.getName());
        valueDescription.setThreadName("CommonServiceValue");
        valueDescription.setPipeLineName(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED);
        valueDescription.setWeight(Integer.MAX_VALUE);
        addValueDescription(valueDescription);

        //初始化管道任务TestDemoTask
        ValueDescription testDemoValueDescription = new ValueDescription();
        testDemoValueDescription.setClassName(TestDemoTask.class.getName());
        testDemoValueDescription.setThreadName("TestDemoTask");
        testDemoValueDescription.setPipeLineName(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED);
        testDemoValueDescription.setWeight(1);
        addValueDescription(testDemoValueDescription);

        //初始化管道任务TestDemoTask2
        ValueDescription testDemoValueDescription2 = new ValueDescription();
        testDemoValueDescription2.setClassName(TestDemoTask2.class.getName());
        testDemoValueDescription2.setThreadName("TestDemoTask2");
        testDemoValueDescription2.setPipeLineName(MsgCodeConstants.PIPELINE_FRAMEWORK_CLIENT_STARTED);
        testDemoValueDescription2.setWeight(1);
        addValueDescription(testDemoValueDescription2);

        //初始化管道任务TestDemoTask3
        ValueDescription testDemoValueDescription3 = new ValueDescription();
        testDemoValueDescription3.setClassName(TestDemoTask3.class.getName());
        testDemoValueDescription3.setThreadName("TestDemoTask3");
        testDemoValueDescription3.setPipeLineName(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED);
        testDemoValueDescription3.setWeight(2);
        addValueDescription(testDemoValueDescription3);
    }
}
