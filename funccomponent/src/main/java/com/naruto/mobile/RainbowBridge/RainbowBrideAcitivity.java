package com.naruto.mobile.RainbowBridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.naruto.mobile.R;
import com.naruto.mobile.base.RainbowBride.RainbowBridge;
import com.naruto.mobile.base.RainbowBride.core.JsBridgeWebChromeClient;

public class RainbowBrideAcitivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rainbow_brider);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        RainbowBridge.getInstance()
                .clazz(JsInvokeJavaScope.class)
                .inject();
        mWebView.setWebChromeClient(new JsBridgeWebChromeClient());
        mWebView.loadUrl("file:///android_asset/test.html");

    }
}
