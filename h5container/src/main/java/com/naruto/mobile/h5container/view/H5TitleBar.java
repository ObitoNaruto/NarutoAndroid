
package com.naruto.mobile.h5container.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.util.H5Log;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5TitleBar implements H5Plugin {
    public static final String TAG = "H5TitleBar";

    private H5Page h5Page;

    private View content;
    private View optionMenu;
    private TextView tvTitle;
    private TextView tvSubtitle;

    private Button btText;
    private ImageButton btImage;

    private View dotView;
    
    private View mBackView;
    
    @SuppressLint("InflateParams")
    public H5TitleBar(H5Page page) {
        h5Page = page;
        Context context = h5Page.getContext().getContext();
        content = LayoutInflater.from(context).inflate(R.layout.h5_title_bar,
                null);
        optionMenu = content.findViewById(R.id.h5_nav_options);
        hideOptionMenu();

        tvTitle = (TextView) content.findViewById(R.id.tv_h5_title);
        tvTitle.setOnClickListener(listener);

        tvSubtitle = (TextView) content.findViewById(R.id.tv_h5_subtitle);
        tvSubtitle.setVisibility(View.GONE);
        tvSubtitle.setOnClickListener(listener);

        btText = (Button) content.findViewById(R.id.bt_h5_text);

        btImage = (ImageButton) content.findViewById(R.id.bt_h5_image);

        dotView = content.findViewById(R.id.bt_h5_dot);
        mBackView = content.findViewById(R.id.h5_titlebar_back);

        btText.setOnClickListener(listener);
        btImage.setOnClickListener(listener);

        hideOptionMenu();
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            String eventName = null;
            if (view.equals(btImage) || view.equals(btText)) {
                eventName = H5Plugin.H5_TITLEBAR_OPTIONS;
                if (dotView != null && dotView.getVisibility() == View.VISIBLE) {
                    dotView.setVisibility(View.GONE);
                }
            } else if (view.equals(tvSubtitle)) {
                eventName = H5Plugin.H5_TITLEBAR_SUBTITLE;
            } else if (view.equals(tvTitle)) {
                eventName = H5Plugin.H5_TITLEBAR_TITLE;
            }

            if (!TextUtils.isEmpty(eventName)) {
                h5Page.sendIntent(eventName, null);
            }
        }
    };

    public View getContent() {
        return content;
    }
    
    public View getBackView() {
    	return mBackView;
    }
    
    @Override
    public void onRelease() {
        h5Page = null;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_TITLE_BAR.equals(action)) {
            showTitleBar();
        } else if (HIDE_TITLE_BAR.equals(action)) {
            hideTitleBar();
        } else if (SHOW_OPTION_MENU.equals(action)) {
            showOptionMenu();
        } else if (SET_OPTION_MENU.equals(action)) {
            setOptionMenu(intent);
        } else if (HIDE_OPTION_MENU.equals(action)) {
            hideOptionMenu();
        } else if (SET_TITLE.equals(action)) {
            setTitle(intent);
        } else if (H5_SHOW_TIPS.equals(action)) {
            H5Tip.showTip(h5Page.getContext().getContext(),
                    (ViewGroup) content, H5Utils.getString(intent.getParam(),
                            H5Container.KEY_TIP_CONTENT));
        }
        return true;
    }

    private void setTitle(H5Intent intent) {
        JSONObject param = intent.getParam();
        if (param == null || param.isEmpty()) {
            return;
        }

        String title = H5Utils.getString(param, "title");
        String subtitle = H5Utils.getString(param, "subtitle");

        if (title != null) {
            tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(subtitle)) {
            tvSubtitle.setVisibility(View.VISIBLE);
            if (subtitle.length() > 5) {
                subtitle = subtitle.substring(0, 4) + "...";
            }
            tvSubtitle.setText(subtitle);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }
    }

    private void showTitleBar() {
        content.setVisibility(View.VISIBLE);
    }

    private void hideTitleBar() {
        content.setVisibility(View.GONE);
    }

    private void setOptionMenu(H5Intent intent) {
        JSONObject param = intent.getParam();
        if (param == null || param.isEmpty()) {
            return;
        }

        String text = H5Utils.getString(param, "title");
        String icon = H5Utils.getString(param, "icon");

        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(icon)) {
            return;
        }

        showOptionMenu();
        if (!TextUtils.isEmpty(text)) {
            btText.setText(text);
            btText.setVisibility(View.VISIBLE);
            btImage.setVisibility(View.GONE);
            btImage.setImageDrawable(null);
        } else if (!TextUtils.isEmpty(icon)) {
            btText.setVisibility(View.GONE);
            btText.setText(null);
            btImage.setVisibility(View.VISIBLE);
            getUrlImage(icon);
        }
    }

    private void showOptionMenu() {
        optionMenu.setVisibility(View.VISIBLE);
    }

    private void hideOptionMenu() {
        optionMenu.setVisibility(View.GONE);
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_TITLE_BAR);
        filter.addAction(HIDE_TITLE_BAR);
        filter.addAction(SHOW_OPTION_MENU);
        filter.addAction(HIDE_OPTION_MENU);
        filter.addAction(SET_OPTION_MENU);
        filter.addAction(SET_TITLE);
        filter.addAction(H5_SHOW_TIPS);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    private void getUrlImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = getImage(url);
                if (data != null) {
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    setButtonImage(image);
                }
            }
        }).start();
    }

    private void setButtonImage(final Bitmap bitmap) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                btImage.setImageBitmap(bitmap);
            }
        });
    }

    public byte[] getImage(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(imageUrl);
        } catch (Exception e) {
            H5Log.e(TAG, "get url exception.", e);
            return null;
        }
        HttpURLConnection conn = null;

        ByteArrayOutputStream bos = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                return null;
            }

            InputStream is = conn.getInputStream();

            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            is.close();
        } catch (Exception e) {

        }
        if (bos != null) {
            return bos.toByteArray();
        } else {
            return null;
        }
    }

}
