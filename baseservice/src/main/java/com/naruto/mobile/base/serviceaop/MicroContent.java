package com.naruto.mobile.base.serviceaop;

import android.content.SharedPreferences;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public interface MicroContent {
    /**
     * ����״̬
     *
     * @param editor
     */
    void saveState(SharedPreferences.Editor editor);

    /**
     * �ָ�״̬
     * @param preferences
     */
    void restoreState(SharedPreferences preferences);
}
