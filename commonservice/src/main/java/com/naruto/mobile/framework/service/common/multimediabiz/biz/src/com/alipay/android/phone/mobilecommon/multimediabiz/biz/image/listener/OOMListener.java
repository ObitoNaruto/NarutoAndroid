package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.listener;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * Created by xiangui.fxg on 2015/5/22.
 */
public class OOMListener {
    public boolean OnCutImageOOm(OutOfMemoryError oom,String path,long size,BitmapCacheLoader cache){
        if(cache != null){
            cache.releaseImageMem(size,true);

            System.gc();
            // 2.3以后GC是异步的,这里加入yield帮助gc回收掉无人引用的内存
            Thread.yield();
            System.gc();
            UCLogUtil.UC_MM_C10(0,0);
            return true;
        }

        return false;
    }
}
