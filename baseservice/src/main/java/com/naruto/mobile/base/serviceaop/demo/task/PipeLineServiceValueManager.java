package com.naruto.mobile.base.serviceaop.demo.task;

import android.util.Log;

import com.naruto.mobile.base.serviceaop.msg.MsgCodeConstants;
import com.naruto.mobile.base.threadpool.AsyncTaskExecutor;
import com.naruto.mobile.base.threadpool.PipeLine;
import com.naruto.mobile.base.threadpool.StandardPipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * 管道任务管理器
 */

public class PipeLineServiceValueManager {

    private Map<String, PipeLine> mPipeLines;
    private AsyncTaskExecutor mAsyncTaskExecutor;

    private static PipeLineServiceValueManager mInstance;

    private PipeLineServiceValueManager() {
        mAsyncTaskExecutor = AsyncTaskExecutor.getInstance();
        mPipeLines = new HashMap<>();
        init();
    }

    private void init() {
        //初始化多个管道, 定义多少个管道由业务系统需求决定
        PipeLine pipeLine_1 = new StandardPipeline(mAsyncTaskExecutor.getExecutor());
        mPipeLines.put(MsgCodeConstants.PIPELINE_FRAMEWORK_INITED, pipeLine_1);


        PipeLine pipeLine_2 = new StandardPipeline(mAsyncTaskExecutor.getExecutor());
        mPipeLines.put(MsgCodeConstants.PIPELINE_FRAMEWORK_CLIENT_STARTED, pipeLine_2);


        PipeLine pipeLine_3 = new StandardPipeline(mAsyncTaskExecutor.getExecutor());
        mPipeLines.put(MsgCodeConstants.PIPELINE_TABLAUNCHER_ACTIVATED, pipeLine_3);

    }

    public static synchronized PipeLineServiceValueManager getInstance() {
        if (mInstance == null) {
            mInstance = new PipeLineServiceValueManager();
        }
        return mInstance;
    }

    public void addTask(String pipeLineName, Runnable task, String threadName, int weight) {
        Log.d("xxm", "Pipeline:" + getPipelineByName(pipeLineName) );
        getPipelineByName(pipeLineName).addTask(task, threadName, weight);
    }

    public PipeLine getPipelineByName(String pipeLineName) {
        return mPipeLines.get(pipeLineName);
    }


    public void start(String pipeLineName) {
        Log.d("xxm", "pipeLineName:" + pipeLineName );
        getPipelineByName(pipeLineName).start();
    }
}
