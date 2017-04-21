package com.naruto.mobile.base.Router.andRouter;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.naruto.mobile.base.Router.andRouter.interceptor.Interceptor;
import com.naruto.mobile.base.Router.andRouter.route.IRoute;
import com.naruto.mobile.base.Router.andRouter.router.ActivityRouter;
import com.naruto.mobile.base.Router.andRouter.router.BrowserRouter;
import com.naruto.mobile.base.Router.andRouter.router.HistoryItem;
import com.naruto.mobile.base.Router.andRouter.router.IActivityRouteTableInitializer;
import com.naruto.mobile.base.Router.andRouter.router.IRouter;

import timber.log.Timber;

/**
 * router 应该是个单例
 */
public class RouterManager {

    private static final RouterManager singleton = new RouterManager();

    //注意这是个list是有顺序的，所以排在前面的优先级会比较高
    static List<IRouter> mRouters = new LinkedList<>();

    private RouterManager(){}

    static RouterManager getSingleton(){
        return singleton;
    }

    public synchronized void addRouter(IRouter router){
        if(router != null){
            //first remove all the duplicate routers
            List<IRouter> duplicateRouters = new ArrayList<>();
            for(IRouter r : mRouters){
                if(r.getClass().equals(router.getClass())){
                    duplicateRouters.add(r);
                }
            }
            mRouters.removeAll(duplicateRouters);
            mRouters.add(router);
        } else {
            Timber.e(new NullPointerException("The Router" +
                    "is null" +
                    ""), "");
        }
    }

    public void setInterceptor(Interceptor interceptor){
        for(IRouter router : mRouters){
            router.setInterceptor(interceptor);
        }
    }

    public synchronized void initBrowserRouter(Context context){
        BrowserRouter browserRouter = BrowserRouter.getInstance();
        browserRouter.init(context);
        addRouter(browserRouter);
    }


    public synchronized void initActivityRouter(Context context){
        ActivityRouter activityRouter = ActivityRouter.getInstance();
        activityRouter.init(context);
        addRouter(activityRouter);
    }

    public synchronized void initActivityRouter(Context context, String ... schemes){
        initActivityRouter(context, null, schemes);
    }

    public synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer, String ... schemes){
        ActivityRouter router = ActivityRouter.getInstance();
        if(initializer == null) {
            router.init(context);
        } else {
            router.init(context, initializer);
        }
        //这里设置支持的scheme
        if(schemes != null && schemes.length > 0){
            router.setMatchSchemes(schemes);//格式化存储支持的scheme，可以是多个
        }
        addRouter(router);
    }

    public List<IRouter> getRouters(){
        return mRouters;
    }


    public boolean open(String url){
        for(IRouter router : mRouters){
            if(router.canOpenTheUrl(url)){//当前url的scheme是匹配的类型
                return router.open(url);
            }
        }
        return false;
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     * @param url
     * @return
     */
    @Nullable
    public IRoute getRoute(String url){
        for(IRouter router : mRouters){
            if(router.canOpenTheUrl(url)){
                return router.getRoute(url);
            }
        }
        return null;
    }

    /**
     * @param url:activity://second/汤二狗
     * @return
     */
    public boolean open(Context context, String url){
        //遍历router得到合适的router打开当前的url
        for(IRouter router : mRouters){
            if(router.canOpenTheUrl(url)){//原理就是比较当前router支持的scheme和当前url的scheme进行比较，相同则说明可以打开
                return router.open(context, url);////这里以ActivityRouter为例,
            }
        }
        return false;
    }


    public boolean openRoute(IRoute route){
        for(IRouter router : mRouters){
            if(router.canOpenTheRoute(route)){
                return router.open(route);
            }
        }
        return false;
    }

    public Queue<HistoryItem> getActivityChangedHistories(){
        ActivityRouter aRouter = null;
        for(IRouter router : mRouters){
            if(router instanceof ActivityRouter){
                aRouter = (ActivityRouter) router;
                break;
            }
        }
        return aRouter != null ? aRouter.getRouteHistories() : null;
    }




}
