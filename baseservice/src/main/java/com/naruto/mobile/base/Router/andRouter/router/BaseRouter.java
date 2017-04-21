package com.naruto.mobile.base.Router.andRouter.router;

import android.content.Context;

import com.naruto.mobile.base.Router.andRouter.interceptor.Interceptor;
import com.naruto.mobile.base.Router.andRouter.route.IRoute;

/**
 */
public abstract class BaseRouter implements IRouter{

    protected static Class<? extends IRoute> CAN_OPEN_ROUTE;

    Interceptor interceptor = null;


    protected Context mBaseContext;


    public void init(Context context){
        mBaseContext = context;
    }

    @Override
    public void setInterceptor(Interceptor interceptor){
        this.interceptor = interceptor;
    }




}
