package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Created by jinmin on 15/4/28.
 */
public class ViewAssistant {

    private Logger logger = Logger.getLogger("ViewAssistant");

    private volatile static ViewAssistant instance;
    protected ViewAssistant(){

    }

    /** Returns singleton class instance */
    public static ViewAssistant getInstance() {
        if (instance == null) {
            synchronized (ViewAssistant.class) {
                if (instance == null) {
                    instance = new ViewAssistant();
                }
            }
        }
        return instance;
    }


    private Map<Integer, Object> mViewCache = new ConcurrentHashMap<Integer, Object>();

    public Integer getViewKey(View view) {
        if (view != null) {
            return view.hashCode();
        }
        return -Integer.MAX_VALUE;
    }

    public Object getViewTag(View view) {
        return mViewCache.get(getViewKey(view));
    }

    /**
     *
     * @param viewWrapper
     * @return
     */
    public boolean checkViewReused(ViewWrapper viewWrapper) {
        if (viewWrapper != null && viewWrapper.getTargetView() != null) {
            Object tag = getViewTag(viewWrapper.getTargetView());
//        log(TAG, "checkImageViewReused tag: " + tag + ", mImageView.getTag(): " + mImageView.getTag());
            boolean ret = (tag == null || !tag.equals(viewWrapper.getTag()));
            logger.p("checkViewReused v: " + getViewKey(viewWrapper.getTargetView())
                    + ", view:" + viewWrapper.getTargetView()
                    + ", viewWrapper:" + viewWrapper
                    + ", tag: " + tag + ", isReused: " + ret);
            if (ret) {
                //logger.e(new Exception("" + tag), "viewWrapper.getTag(): " + viewWrapper.getTag());
            }
            return ret;
        }
        return false;
    }

    public void setViewTag(View v, Object tag) {
        if (tag != null && v != null) {
            //其实就是hashcode值
            Integer viewKey = getViewKey(v);
            logger.p("setViewTag v: " + viewKey + ", tag: " + tag);
            mViewCache.put(viewKey, tag);
        }
    }

    public void removeViewTag(View v) {
        mViewCache.remove(getViewKey(v));
    }

    public static boolean checkImageViewNeedRender(View view, Drawable drawable) {
        if (view instanceof ImageView && drawable instanceof BitmapDrawable) {
            Drawable viewDrawable = ((ImageView)view).getDrawable();
            if (viewDrawable instanceof BitmapDrawable) {
                BitmapDrawable viewBitmapDrawable = (BitmapDrawable)viewDrawable;
                return viewBitmapDrawable.getBitmap() != ((BitmapDrawable)drawable).getBitmap();
            }
        }
        return true;
    }

    public static boolean checkImageViewNeedRender(View view, Bitmap bitmap) {
        if (view instanceof ImageView && bitmap != null) {
            Drawable viewDrawable = ((ImageView)view).getDrawable();
            if (viewDrawable instanceof BitmapDrawable) {
                BitmapDrawable viewBitmapDrawable = (BitmapDrawable)viewDrawable;
                return viewBitmapDrawable.getBitmap() != bitmap;
            }
        }
        return true;
    }
}
