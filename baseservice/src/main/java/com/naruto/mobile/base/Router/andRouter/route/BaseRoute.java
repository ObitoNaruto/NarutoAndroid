package com.naruto.mobile.base.Router.andRouter.route;

import java.util.List;
import java.util.Map;

import com.naruto.mobile.base.Router.andRouter.router.IRouter;
import com.naruto.mobile.base.Router.andRouter.utils.UrlUtils;

/**
 * Created by kris on 16/3/16.
 */
public abstract class BaseRoute implements IRoute {
    IRouter mRouter;
    String mUrl;
    String mScheme;
    String mHost;
    int mPort;
    List<String> mPath;
    Map<String, String> mQueryParameters;

    /**
     * activity://main/123/path1?des=hello
     * @param router
     * @param url
     */
    public BaseRoute(IRouter router, String url){
        mRouter = router;
        mUrl = url;
        mScheme = UrlUtils.getScheme(url);//activity
        mHost = UrlUtils.getHost(url);//main
        mPort = UrlUtils.getPort(url);
        mPath = UrlUtils.getPathSegments(url);//123/path1
        mQueryParameters = UrlUtils.getParameters(url);//des=hello

    }

    @Override
    public IRouter getRouter() {
        return mRouter;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getScheme() {
        return mScheme;
    }

    @Override
    public String getHost() {
        return mHost;
    }

    @Override
    public int getPort() {
        return mPort;
    }

    @Override
    public List<String> getPath() {
        return mPath;
    }

    @Override
    public Map<String, String> getParameters(){
        return mQueryParameters;
    }


    @Override
    public boolean open() {
        return mRouter.open(this);
    }
}
