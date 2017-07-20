package com.naruto.mobile.rpc.volley;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import com.alibaba.fastjson.JSON;
import com.naruto.mobile.R;
import com.naruto.mobile.base.rpc.volley.VolleySingleton;

import org.json.JSONObject;

public class MarkingStandardRequestActivity extends AppCompatActivity {

    private ImageView mIvTest;

    private TextView mTvTest;

    private static final String url = "http://i.imgur.com/7spzG.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marking_standard_request);
        mIvTest = (ImageView) findViewById(R.id.iv_test);
        mTvTest = (TextView) findViewById(R.id.tv_test);
        loadImageRequest2();
        loadJsonRequest();
    }

    private void loadImageRequest() {
        //请求图片的第一种实现
        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mIvTest.setImageBitmap(bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                mIvTest.setImageResource(R.mipmap.ic_launcher);
            }
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void loadImageRequest2() {
        // The URL for the image that is being loaded.
        final String IMAGE_URL =
                "https://developer.android.com/images/training/system-ui.png";
        VolleySingleton.getInstance(this).getImageLoader()
                .get(IMAGE_URL, ImageLoader.getImageListener(mIvTest, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
    }

    private void loadJsonRequest() {
        String url = "https://www.baidu.com";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTvTest.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                mTvTest.setText(error.getMessage());
            }
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
