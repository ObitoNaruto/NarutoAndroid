package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileRsp;

public class FileFutureTask extends FutureTask<APFileRsp> {

    private FileTask fileTask;

    public FileFutureTask(FileTask fileTask) {
        super(fileTask);
        this.fileTask = fileTask;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.fileTask != null) {
            this.fileTask.cancel();
        }
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public APFileRsp get() throws InterruptedException, ExecutionException {
        return super.get();
    }
}
