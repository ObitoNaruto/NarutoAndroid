package com.naruto.mobile.base.Router.andRouter.router;

import android.app.Activity;

import java.util.Map;

/**
 */
public interface IActivityRouteTableInitializer {
    /**
     * init the router table
     * @param router the router map to
     */
    void initRouterTable(Map<String, Class<? extends Activity>> router);



}
