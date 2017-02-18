package com.naruto.mobile.binderPool;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.naruto.mobile.R;
import com.naruto.mobile.base.binderPool.BinderPoolManager;
import com.naruto.mobile.base.binderPool.ComputeImpl;
import com.naruto.mobile.base.binderPool.ICompute;
import com.naruto.mobile.base.binderPool.ISecurityCenter;
import com.naruto.mobile.base.binderPool.SecurityCenterImpl;

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";

    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        new Thread(new Runnable() {

            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    //CountDownLatch将bindService这一异步操作转换成了同步操作,这就意味着它是可能耗时的，然后就是Binder方法的调用过程也可能是耗时的，因此不建议放到主线程中去执行
    private void doWork() {
        BinderPoolManager binderPool = BinderPoolManager.getInsance(BinderPoolActivity.this);
        IBinder securityBinder = binderPool
                .queryBinder(BinderPoolManager.BINDER_SECURITY_CENTER);
        mSecurityCenter = (ISecurityCenter) SecurityCenterImpl
                .asInterface(securityBinder);
        Log.d(TAG, "visit ISecurityCenter");
        String msg = "helloworld-安卓";
        System.out.println("content:" + msg);
        try {
            String password = mSecurityCenter.encrypt(msg);
            System.out.println("encrypt:" + password);
            System.out.println("decrypt:" + mSecurityCenter.decrypt(password));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "visit ICompute");
        IBinder computeBinder = binderPool
                .queryBinder(BinderPoolManager.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);
        try {
            System.out.println("3+5=" + mCompute.add(3, 5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
