package com.naruto.mobile.base.Router.andRouter.route;

import java.util.List;
import java.util.Map;

import com.naruto.mobile.base.Router.andRouter.router.IRouter;

/**
 */
public interface IRoute {

    /**
     * get the Router to process the Route
     *
     * @return
     */
    IRouter getRouter();


    String getUrl();

    String getScheme();

    String getHost();

    int getPort();

    List<String> getPath();

    Map<String, String> getParameters();

    //Route can open itself

    /**
     *
     * @return true: open success, false : open fail
     */
    boolean open();

}
