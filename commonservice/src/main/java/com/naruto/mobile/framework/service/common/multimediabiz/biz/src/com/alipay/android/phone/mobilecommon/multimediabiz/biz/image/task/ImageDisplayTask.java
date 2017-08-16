package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.concurrent.Future;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.ReusableBitmapDrawable;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ImageDisplayUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.ViewWrapper;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 显示任务
 */
public class ImageDisplayTask extends ImageTask {

    private Logger logger = Logger.getLogger("ImageDisplayTask");

    private Bitmap bitmap;
    private boolean reusableBitmap;

    public ImageDisplayTask(Bitmap bitmap, ImageLoadReq req, boolean reusableBitmap, ViewWrapper wrapper) {
        super(req, wrapper);
        this.bitmap = bitmap;
        this.reusableBitmap = reusableBitmap;
    }

    public ImageDisplayTask(Bitmap bitmap, ImageLoadReq req) {
        this(bitmap, req, true, null);
        this.viewAssistant.setViewTag(req.imageView, req.cacheKey);
    }

    public ImageDisplayTask(Bitmap bitmap, ImageLoadReq req, ViewWrapper viewWrapper) {
        this(bitmap, req, true, viewWrapper);
    }

    /**
     * 异常参数显示所用
     * @param req
     */
    public ImageDisplayTask(ImageLoadReq req) {
        super(req, null);
    }

    @Override
    public Object call() {
        //显示
        if (ImageUtils.checkBitmap(bitmap)) {//bitmap非空且有效
            display(bitmap);
        } else {
            display(loadReq.options.getImageOnLoading());//取设置进来的加载中图片
            notifyError(APImageRetMsg.RETCODE.PARAM_ERROR, "param err", null);
        }
        return null;
    }

    private void display(final Drawable drawable) {
        if (options.getDisplayer() != null) {
            ImageDisplayUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (checkImageViewReused()) {
                        logger.p("displayer drawable checkImageViewReused return !");
                        return;
                    }
                    options.getDisplayer().display(loadReq.imageView, drawable, loadReq.source);
                }
            });
        } else {
            ImageDisplayUtils.display(drawable, viewWrapper);
        }
    }

    private void display(final Bitmap bitmap) {
        if (checkImageViewReused()) {
            logger.d("display task: " + loadReq.cacheKey + ", view: " + viewWrapper.getTargetView() + ", reused!!!");
            notifyReuse();
            return;
        }
        if (options.getDisplayer() != null) {//自定义显示回调不为空
            logger.p("display with displayer");
            showWithDisplayer(bitmap);
        } else {
            logger.p("display without displayer");
            ImageDisplayUtils.display(bitmap, viewWrapper, reusableBitmap);//可重复使用的标记-reusableBitmap,默认值是fasle，不可再用
        }
        notifySuccess();
    }

    private void showWithDisplayer(final Bitmap bitmap) {
        ImageDisplayUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkImageViewReused()) {
                    logger.p("displayer bitmap checkImageViewReused return !");
                    return;
                }
                BitmapDrawable d = reusableBitmap ? new ReusableBitmapDrawable(AppUtils.getResources(), bitmap) :
                                        new BitmapDrawable(AppUtils.getResources(), bitmap);
                options.getDisplayer().display(viewWrapper.getTargetView(),
                        d, loadReq.source);
            }
        });
    }

    public Future runTask(boolean sync, ImageTaskEngine engine) {
        Future task = null;
        if (sync || options.isSyncLoading()) {//同步执行
            call();
        } else if (engine != null) {//提交displayEngine，异步执行
            task = engine.submit(this);
        }
        return task;
    }

    public Future runTask() {
        return runTask(false, ImageTaskEngine.get());
    }

    public void syncRunTask() {
        runTask(true, ImageTaskEngine.get());
    }

}
