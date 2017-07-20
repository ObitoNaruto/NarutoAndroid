package com.naruto.mobile.rpc.volley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.naruto.mobile.R;

public class SendSimpleRequestAcvity extends AppCompatActivity {

    private TextView mTextTv;

    public static final String TAG = "MyTag";
    StringRequest mStringRequest; // Assume this exists.
    RequestQueue mRequestQueue; // Assume this exists.
    // Set the tag on the request.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_simple_request_acvity);
        mTextTv = (TextView) findViewById(R.id.text_tv);
        loadData();
    }

    private void loadData() {
        // Instantiate the RequestQueue.
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://www.baidu.com/";
        // Request a string response from the provided URL.
        mStringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextTv.setText("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextTv.setText("That didn't work!");
            }
        });
        mStringRequest.setTag(TAG);
        // Add the request to the RequestQueue.
        mRequestQueue.add(mStringRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(TAG);
        }
        /**
         * 可以为你的所有请求都绑定到执行的Activity上，然后你可以在onStop()方法执行requestQueue.cancelAll(this)
         */
        //requestQueue.cancelAll(this)
    }
}
