
package com.naruto.mobile.h5container.api;

import android.os.Bundle;
import android.view.View;

public interface H5Page extends H5CoreNode {

    public interface H5PageHandler {
        // the page will exit if return true
        public boolean shouldExit();
    }

    public H5Session getSession();

    public H5Context getContext();

    public View getContentView();

    public void loadUrl(String url);

    public void loadDataWithBaseURL(String baseUrl, String data,
            String mimeType, String encoding, String historyUrl);

    public void setTextSize(int textSize);

    public String getUrl();

    public String getTitle();

    public H5Bridge getBridge();

    public Bundle getParams();

    public boolean exitPage();

    public void setHandler(H5PageHandler handler);

}
