package com.naruto.mobile.base.serviceaop;

import android.content.SharedPreferences;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public interface MicroContent {
    /**
     * 保存状态
     * @param editor
     */
    void saveState(SharedPreferences.Editor editor);

    /**
     * 恢复状态
     * @param preferences
     */
    void restoreState(SharedPreferences preferences);
}
