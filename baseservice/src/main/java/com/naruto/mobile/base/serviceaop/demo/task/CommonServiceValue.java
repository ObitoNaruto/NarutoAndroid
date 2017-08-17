package com.naruto.mobile.base.serviceaop.demo.task;

import android.util.Log;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;
import com.naruto.mobile.base.serviceaop.demo.service.TaskScheduleService;
import com.naruto.mobile.base.serviceaop.msg.MsgCodeConstants;
import com.naruto.mobile.base.threadpool.PipeLine;

/**
 * 管道task任务入口task
 */

public class CommonServiceValue implements Runnable {

    private final static String TAG = CommonServiceValue.class.getSimpleName();
    @Override
    public void run() {
        NarutoApplicationContext narutoApplicationContext = NarutoApplication.getInstance().getNarutoApplicationContext();

        if(narutoApplicationContext == null){
            Log.d(TAG, "narutoApplicationContext is null");
            return;
        }

        final TaskScheduleService taskScheduleService = narutoApplicationContext.findServiceByInterface(TaskScheduleService.class.getName());

        if(taskScheduleService == null) {
            Log.d(TAG, "TaskScheduleService is null");
            return;
        }

        final PipeLine pipeline_1 = narutoApplicationContext
                .getPipelineByName(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED);
        pipeline_1.addIdleListener(new Runnable() {

            @Override
            public void run() {
                taskScheduleService
                        .onPipelineFinished(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED);
                pipeline_1.addIdleListener(null);
            }
        });

        final PipeLine pipeline_2 = narutoApplicationContext
                .getPipelineByName(MsgCodeConstants.PIPELINE_FRAMEWORK_CLIENT_STARTED);
        pipeline_2.addIdleListener(new Runnable() {

            @Override
            public void run() {
                taskScheduleService
                        .onPipelineFinished(MsgCodeConstants.PIPELINE_FRAMEWORK_CLIENT_STARTED);
                pipeline_2.addIdleListener(null);
            }
        });

        final PipeLine pipeline_3 = narutoApplicationContext
                .getPipelineByName(MsgCodeConstants.PIPELINE_TABLAUNCHER_ACTIVATED);
        pipeline_3.addIdleListener(new Runnable() {

            @Override
            public void run() {
                taskScheduleService
                        .onPipelineFinished(MsgCodeConstants.PIPELINE_TABLAUNCHER_ACTIVATED);
                pipeline_3.addIdleListener(null);
            }
        });
    }
}
