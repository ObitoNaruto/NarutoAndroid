
package com.naruto.mobile.h5container.refresh;

import android.view.View;

public interface H5PullAdapter {

    public boolean canPull();

    public boolean canRefresh();

    public View getHeaderView();

    public void onOpen();

    public void onOver();

    public void onLoading();

    public void onFinish();

}
