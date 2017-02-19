package com.naruto.mobile.CountDownTimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naruto.mobile.R;

public class CountDownTimerTestActivity extends AppCompatActivity {

    private CountDownTimerManager mCountDownTimerManager;
    private TextView mTextView;
    private Button mButton;

    private CountDownTimerManager.OnTimeCountListener mOnTimeCountListener = new CountDownTimerManager.OnTimeCountListener() {
        @Override
        public void onTick(long secondUntilFinished) {
            mButton.setEnabled(false);
            mButton.setClickable(false);
            mTextView.setText(secondUntilFinished + "s");
        }

        @Override
        public void onFinish() {
            mButton.setEnabled(true);
            mButton.setClickable(true);
            mTextView.setText("默认值");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down_timer_test);
        mTextView = (TextView) findViewById(R.id.tv_test);
        mButton = (Button)findViewById(R.id.btn_start);
        mCountDownTimerManager = new CountDownTimerManager(CountDownTimerManager.TOTAL_TIME, CountDownTimerManager.INTERVAL, mOnTimeCountListener);
    }

    public void onClickStart(View view){
        mCountDownTimerManager.start();

    }

    @Override
    protected void onDestroy() {
        mCountDownTimerManager.cancel();
        super.onDestroy();
    }
}
