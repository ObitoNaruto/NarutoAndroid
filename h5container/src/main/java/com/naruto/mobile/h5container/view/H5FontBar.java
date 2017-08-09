
package com.naruto.mobile.h5container.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.api.H5Scenario;
import com.naruto.mobile.h5container.env.H5Container;

public class H5FontBar implements OnClickListener, H5Plugin {

    public static final String TAG = "H5FontBar";

    public static final String SHOW_FONT_BAR = "showFontBar";
    public static final String HIDE_FONT_BAR = "hideFontBar";

    private View fontBlankView;
    private View fontToolBar;
    private View fontSize1;
    private View fontSize2;
    private View fontSize3;
    private View fontSize4;
    private View fontBarClose;

    private ImageView ivSize1;
    private ImageView ivSize2;
    private ImageView ivSize3;
    private ImageView ivSize4;
    private PopupWindow window;
    private View contentView;
    private View rootView;
    private H5Page h5Page;

    public H5FontBar(H5Page page) {
        this.h5Page = page;
        Activity activity = (Activity) page.getContext().getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);

        contentView = layoutInflater.inflate(R.layout.h5_font_bar, null);
        rootView = activity.getWindow().getDecorView()
                .findViewById(android.R.id.content);

        fontBlankView = contentView.findViewById(R.id.h5_font_blank);
        fontBlankView.setOnClickListener(this);

        fontToolBar = contentView.findViewById(R.id.h5_font_bar);
        fontToolBar.setOnClickListener(this);

        ivSize1 = (ImageView) contentView.findViewById(R.id.iv_font_size1);
        ivSize2 = (ImageView) contentView.findViewById(R.id.iv_font_size2);
        ivSize3 = (ImageView) contentView.findViewById(R.id.iv_font_size3);
        ivSize4 = (ImageView) contentView.findViewById(R.id.iv_font_size4);
        fontBarClose = contentView.findViewById(R.id.h5_font_close);

        fontSize1 = contentView.findViewById(R.id.h5_font_size1);
        fontSize2 = contentView.findViewById(R.id.h5_font_size2);
        fontSize3 = contentView.findViewById(R.id.h5_font_size3);
        fontSize4 = contentView.findViewById(R.id.h5_font_size4);

        fontSize1.setOnClickListener(this);
        fontSize2.setOnClickListener(this);
        fontSize3.setOnClickListener(this);
        fontSize4.setOnClickListener(this);

        fontBarClose.setOnClickListener(this);

        int fontSize = H5Container.WEBVIEW_FONT_SIZE_NORMAL;
        H5Scenario scenario = h5Page.getSession().getScenario();
        if (scenario != null) {
            String fontStr = scenario.getData().get(H5Container.FONT_SIZE);
            if (!TextUtils.isEmpty(fontStr)) {
                fontSize = Integer.parseInt(fontStr);
            }
            updateFontBar(fontSize);
        }
    }

    @Override
    public void onClick(View view) {
        int targetFontSize = H5Container.WEBVIEW_FONT_SIZE_INVALID;
        if (view.equals(fontBlankView) || view.equals(fontBarClose)) {
            hideFontBar();
            return;
        } else if (view.equals(fontSize1)) {
            targetFontSize = H5Container.WEBVIEW_FONT_SIZE_SMALLER;
        } else if (view.equals(fontSize2)) {
            targetFontSize = H5Container.WEBVIEW_FONT_SIZE_NORMAL;
        } else if (view.equals(fontSize3)) {
            targetFontSize = H5Container.WEBVIEW_FONT_SIZE_LARGER;
        } else if (view.equals(fontSize4)) {
            targetFontSize = H5Container.WEBVIEW_FONT_SIZE_LARGEST;
        }

        if (targetFontSize == H5Container.WEBVIEW_FONT_SIZE_INVALID) {
            return;
        }

        onFontSizeChanged(targetFontSize);
    }

    private void onFontSizeChanged(int size) {
        JSONObject param = new JSONObject();
        param.put("size", size);
        h5Page.sendIntent(H5Plugin.H5_PAGE_FONT_SIZE, param);
        updateFontBar(size);
    }

    private void updateFontBar(int size) {
        ivSize1.setImageResource(R.drawable.font_size1_enable);
        ivSize2.setImageResource(R.drawable.font_size2_enable);
        ivSize3.setImageResource(R.drawable.font_size3_enable);
        ivSize4.setImageResource(R.drawable.font_size4_enable);
        if (size == H5Container.WEBVIEW_FONT_SIZE_SMALLER) {
            ivSize1.setImageResource(R.drawable.font_size1_disable);
        } else if (size == H5Container.WEBVIEW_FONT_SIZE_NORMAL) {
            ivSize2.setImageResource(R.drawable.font_size2_disable);
        } else if (size == H5Container.WEBVIEW_FONT_SIZE_LARGER) {
            ivSize3.setImageResource(R.drawable.font_size3_disable);
        } else if (size == H5Container.WEBVIEW_FONT_SIZE_LARGEST) {
            ivSize4.setImageResource(R.drawable.font_size4_disable);
        }
    }

    private void showFontBar() {
        if (window == null) {
            Context ctx = contentView.getContext();
            window = new PopupWindow(ctx, null, 0);
            window.setContentView(contentView);
            window.setWidth(rootView.getWidth());
            window.setHeight(rootView.getHeight());
        }
        window.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private void hideFontBar() {
        window.dismiss();
    }

    @Override
    public void onRelease() {
        h5Page = null;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_FONT_BAR.equals(action)) {
            showFontBar();
        } else if (HIDE_FONT_BAR.equals(action)) {
            hideFontBar();
        }
        return true;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_FONT_BAR);
        filter.addAction(HIDE_FONT_BAR);
    }
}
