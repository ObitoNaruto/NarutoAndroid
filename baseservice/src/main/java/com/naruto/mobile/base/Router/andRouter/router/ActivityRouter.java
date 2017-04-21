package com.naruto.mobile.base.Router.andRouter.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.naruto.mobile.base.BuildConfig;
import com.naruto.mobile.base.Router.andRouter.exception.InvalidRoutePathException;
import com.naruto.mobile.base.Router.andRouter.exception.InvalidValueTypeException;
import com.naruto.mobile.base.Router.andRouter.exception.RouteNotFoundException;
import com.naruto.mobile.base.Router.andRouter.route.ActivityRoute;
import com.naruto.mobile.base.Router.andRouter.route.IRoute;
import com.naruto.mobile.base.Router.andRouter.tools.ActivityRouteRuleBuilder;
import com.naruto.mobile.base.Router.andRouter.utils.UrlUtils;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import timber.log.Timber;

import static com.naruto.mobile.base.Router.andRouter.utils.UrlUtils.getHost;
import static com.naruto.mobile.base.Router.andRouter.utils.UrlUtils.getPathSegments;
import static com.naruto.mobile.base.Router.andRouter.utils.UrlUtils.getScheme;

/**
 * Created by kris on 16/3/10.
 */
public class ActivityRouter extends BaseRouter {
    private static final String TAG = "Router";
    private static List<String> MATCH_SCHEMES = new ArrayList<>();
    private static final String DEFAULT_SCHEME = "activity";
    private static final int HISTORY_CACHE_SIZE = 20;

    private static ActivityRouter mActivityRouter = new ActivityRouter();   //Activity

    private static final String KEY_URL = "key_and_activity_router_url";

    static {
        CAN_OPEN_ROUTE = ActivityRoute.class;
        MATCH_SCHEMES.add(DEFAULT_SCHEME);

        try {
            Constructor<?> constructor =  Class.forName("com.naruto.mobile.base.Router.andRouter.router.AnnotatedRouterTableInitializer").getConstructor();
            IActivityRouteTableInitializer initializer = (IActivityRouteTableInitializer) constructor.newInstance();
            mActivityRouter.initActivityRouterTable(initializer);

        } catch (Exception e) {
            //do nothing
        }
    }

    /**
     *url以及与之对应的activity的类对象
     */
    private Map<String, Class<? extends Activity>> mRouteTable = new HashMap<>();
    private CircularFifoQueue<HistoryItem> mHistoryCaches = new CircularFifoQueue<>(HISTORY_CACHE_SIZE);


    public static ActivityRouter getInstance(){
        return mActivityRouter;
    }

    public void init(Context appContext, IActivityRouteTableInitializer initializer) {
        super.init(appContext);//给上下文赋值初始化
        initActivityRouterTable(initializer);
    }

    @Override
    public void init(Context appContext) {
        init(appContext, null);
    }

    public void initActivityRouterTable(IActivityRouteTableInitializer initializer) {
        if(initializer != null) {
            initializer.initRouterTable(mRouteTable);
        }
        for (String pathRule : mRouteTable.keySet()) {
            boolean isValid = ActivityRouteRuleBuilder.isActivityRuleValid(pathRule);
            if (!isValid) {
                Timber.e(new InvalidRoutePathException(pathRule), "");
                mRouteTable.remove(pathRule);
            }
        }
    }


    @Override
    public ActivityRoute getRoute(String url) {
        return new ActivityRoute.Builder(this)//传递当前类对象
                .setUrl(url)//传递当前url
                .build();//构建
    }

    @Override
    public boolean canOpenTheRoute(IRoute route) {
        return CAN_OPEN_ROUTE.equals(route.getClass());
    }

    /**
     *
     * @param url:activity://second/汤二狗
     * @return
     */
    @Override
    public boolean canOpenTheUrl(String url) {
        for(String scheme : MATCH_SCHEMES) {
            if(TextUtils.equals(scheme, getScheme(url))){//如果scheme相同，表示当前router可以打开当前url
                return true;
            }
        }
        return false;
    }

    /**
     * It support multi schemes now
     * @see #getMatchSchemes()
     * @return
     */
    @Deprecated
    public String getMatchScheme() {
        return MATCH_SCHEMES.get(0);
    }

    public List<String> getMatchSchemes(){
        return MATCH_SCHEMES;
    }

    public void setMatchScheme(String scheme) {
        MATCH_SCHEMES.clear();
        MATCH_SCHEMES.add(scheme);
    }

    public void setMatchSchemes(String... schemes){
        MATCH_SCHEMES.clear();
        List<String> list = Arrays.asList(schemes);
        list.remove("");
        list.remove(null);
        MATCH_SCHEMES.addAll(list);
    }

    public void addMatchSchemes(String scheme){
        MATCH_SCHEMES.add(scheme);
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return CAN_OPEN_ROUTE;
    }

    @Override
    public boolean open(IRoute route) {
        boolean ret = false;
        if (route instanceof ActivityRoute) {
            ActivityRoute aRoute = (ActivityRoute) route;
            try {
                switch (aRoute.getOpenType()) {
                    case ActivityRoute.START:
                        //拦截
                        if(doOnInterceptor(aRoute.getActivity(), route.getUrl())){
                            return true;
                        }
                        open(aRoute, aRoute.getActivity());
                        ret = true;
                        break;
                    case ActivityRoute.FOR_RESULT_ACTIVITY:
                        if(doOnInterceptor(aRoute.getActivity(), route.getUrl())){
                            return true;
                        }
                        openForResult(aRoute, aRoute.getActivity(), aRoute.getRequestCode());//activity中以startActivityForResult启动
                        ret = true;
                        break;
                    case ActivityRoute.FOR_RESULT_SUPPORT_FRAGMENT:
                        if(doOnInterceptor(aRoute.getSupportFragment().getActivity(), route.getUrl())){
                            return true;
                        }
                        openForResult(aRoute, aRoute.getSupportFragment(), aRoute.getRequestCode());
                        ret = true;
                        break;
                    case ActivityRoute.FOR_RESULT_FRAGMENT:
                        if(doOnInterceptor(aRoute.getFragment().getActivity(), route.getUrl())){
                            return true;
                        }
                        openForResult(aRoute, aRoute.getFragment(), aRoute.getRequestCode());
                        ret = true;
                        break;
                    default:
                        Timber.e("Error Open Type");
                        ret = false;
                        break;

                }
            } catch (Exception e) {
                Timber.e(e, "Url route not specified: %s", route.getUrl());
                ret = false;
            }
        }
        return ret;

    }

    @Override
    public boolean open(String url) {
        return open(null, url);
    }

    /**
     *
     * @param context
     * @param url :activity://second/汤二狗
     * @return
     */
    @Override
    public boolean open(Context context, String url) {
        //判断当前url是否要被拦截器拦截，拦截并返回true
        if(doOnInterceptor(context, url)){
            return true;
        }
        IRoute route = getRoute(url);//将url封装成ActivityRoute
        if(route instanceof ActivityRoute){
            ActivityRoute aRoute = (ActivityRoute) route;
            try {
                open(aRoute, context);//启动activity
                return true;
            } catch (Exception e){
                Timber.e(e, "Url route not specified: %s", route.getUrl());
            }
        }
        return false;
    }

    /**
     * 判断当前url是否要被拦截器拦截，能拦截走拦截流程
     * @param context
     * @param url
     * @return
     */
    private boolean doOnInterceptor(Context context, String url){
        if(interceptor != null){
            //interceptor的具体实现在app全局上下文中实现
            return interceptor.intercept(context != null ? context : mBaseContext, url);
        }
        return false;
    }


    /**
     * 启动对应的activity
     * @param route
     * @param context
     * @throws RouteNotFoundException
     */
    protected void open(ActivityRoute route, Context context) throws RouteNotFoundException {
        //当前山下文为空，则取全局上下文，全局上下文mBaseContext在application中进行设置
        Class<?> fromClazz = context != null ? context.getClass() : mBaseContext.getClass();
        Intent intent = match(fromClazz, route);//组装intent
        if (intent == null) {//未找到匹配路由，直接抛出异常
            throw new RouteNotFoundException(route.getUrl());
        }
        if (context == null) {//另起任务栈启动目标activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | route.getFlags());
            mBaseContext.startActivity(intent);
        } else {
            intent.setFlags(route.getFlags());
            context.startActivity(intent);//在当前任务栈启动目标activity
        }

        //如果有设置动画，加上动画
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }

    }

    protected void openForResult(ActivityRoute route, Activity activity, int requestCode) throws RouteNotFoundException {
        Intent intent = match(activity.getClass(), route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        intent.setFlags(route.getFlags());
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }
        activity.startActivityForResult(intent, requestCode);

    }

    protected void openForResult(ActivityRoute route, Fragment fragment, int requestCode) throws RouteNotFoundException {

        Intent intent = match(fragment.getClass(), route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        intent.setFlags(route.getFlags());
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }
        fragment.startActivityForResult(intent, requestCode);

    }

    protected void openForResult(ActivityRoute route, android.app.Fragment fragment, int requestCode) throws RouteNotFoundException {

        Intent intent = match(fragment.getClass(), route);
        if (intent == null) {
            throw new RouteNotFoundException(route.getUrl());
        }
        intent.setFlags(route.getFlags());
        if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
            route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
        }
        fragment.startActivityForResult(intent, requestCode);

    }


    /**
     * host 和path匹配称之为路由匹匹配
     * 例：activity://main/:i{key1}/path1/:f{key2}
     * 一个url与路由匹配需要scheme，host以及path中的固定部分相同，而key部分被具体的值代替
     *
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private String findMatchedRoute(ActivityRoute route) {
        List<String> givenPathSegs = route.getPath();
        OutLoop:
        for (String routeUrl : mRouteTable.keySet()) {
            List<String> routePathSegs = getPathSegments(routeUrl);
            //比较host
            if (!TextUtils.equals(getHost(routeUrl), route.getHost())) {
                continue;
            }
            //比较path个数是否相等
            if (givenPathSegs.size() != routePathSegs.size()) {
                continue;
            }
            for (int i = 0; i < routePathSegs.size(); i++) {
                if (!routePathSegs.get(i).startsWith(":")//不能以“：”开头，以“:”开头的是参数
                        && !TextUtils.equals(routePathSegs.get(i), givenPathSegs.get(i))) {
                    continue OutLoop;
                }
            }
            //find the match route
            return routeUrl;
        }

        return null;
    }

    /**
     * find the key value in the path and set them in the intent
     *eg:activity://main/:i{key1}/path1/:f{key2}
     * @param routeUrl the matched route path：设置的匹配路由
     * @param givenUrl the given path：传递进来的路由
     * @param intent   the intent
     * @return the intent
     */
    private Intent setKeyValueInThePath(String routeUrl, String givenUrl, Intent intent) {
        List<String> routePathSegs = getPathSegments(routeUrl);
        List<String> givenPathSegs = getPathSegments(givenUrl);
        //便利匹配路由url的path部分，是一个list列表（因为有多个值）
        for (int i = 0; i < routePathSegs.size(); i++) {
            String seg = routePathSegs.get(i);//取出一个值
            if (seg.startsWith(":")) {//以":'开头，是一个动态的path值，每个业务传递的值都不一样
                int indexOfLeft = seg.indexOf("{");
                int indexOfRight = seg.indexOf("}");
                //eg:key1
                String key = seg.substring(indexOfLeft + 1, indexOfRight);
                /**
                 * key format 	:i{key} 	:f{key} 	:l{key} 	:d{key} 	:s{key} or :{key} 	:c{key}
                 type 	        integer 	float 	    long 	    double 	    string 	            char
                 */
                char typeChar = seg.charAt(1);//
                switch (typeChar) {
                    //interger type
                    case 'i':
                        try {
                            int value = Integer.parseInt(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析整形类型失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                //如果是在release情况下则给一个默认值
                                intent.putExtra(key, 0);
                            }
                        }
                        break;
                    case 'f':
                        //float type
                        try {
                            float value = Float.parseFloat(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析浮点类型失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, 0f);
                            }
                        }
                        break;
                    case 'l':
                        //long type
                        try {
                            long value = Long.parseLong(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析长整形失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, 0l);
                            }
                        }
                        break;
                    case 'd':
                        try {
                            double value = Double.parseDouble(givenPathSegs.get(i));
                            intent.putExtra(key, value);
                        } catch (Exception e) {
                            Log.e(TAG, "解析double类型失败 " + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, 0d);
                            }
                        }
                        break;
                    case 'c':
                        try {
                            char value = givenPathSegs.get(i).charAt(0);
                        } catch (Exception e) {
                            Log.e(TAG, "解析Character类型失败" + givenPathSegs.get(i), e);
                            if (BuildConfig.DEBUG) {
                                throw new InvalidValueTypeException(givenUrl, givenPathSegs.get(i));
                            } else {
                                intent.putExtra(key, ' ');
                            }
                        }
                        break;
                    case 's':
                    default:
                        intent.putExtra(key, givenPathSegs.get(i));
                }
            }

        }
        return intent;
    }

    /**
     *
     * @param url
     * @param intent
     * @return
     */
    private Intent setOptionParams(String url, Intent intent) {
        //key=value这种键值对，url中"?"后面的键值对
        Map<String, String> queryParams = UrlUtils.getParameters(url);
        for (String key : queryParams.keySet()) {
            intent.putExtra(key, queryParams.get(key));
        }
        return intent;
    }

    private Intent setExtras(Bundle bundle, Intent intent) {
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 组装intent
     * @param from：源上下文，值为activity或者applicationContext
     * @param route
     * @return
     */
    @Nullable
    private Intent match(Class<?> from, ActivityRoute route) {
        //找到匹配的路由规则，如果不匹配直接返回
        String matchedRoute = findMatchedRoute(route);//一个url与路由匹配需要scheme，host以及path中的固定部分相同，而key部分被具体的值代替
        if (matchedRoute == null) {
            return null;
        }
        //找到目标路由对应的activity
        Class<? extends Activity> matchedActivity = mRouteTable.get(matchedRoute);
        Intent intent = new Intent(mBaseContext, matchedActivity);
        //缓存一下activity
        mHistoryCaches.add(new HistoryItem(from, matchedActivity));//放入一个先进先出的队列
        //find the key value in the path
        intent = setKeyValueInThePath(matchedRoute, route.getUrl(), intent);//向intent中添加路由path的部分入参
        intent = setOptionParams(route.getUrl(), intent);//向intent中添加路由“？”后面的键值对入参
        intent = setExtras(route.getExtras(), intent);//向intent中添加bundle类型数据
        intent.putExtra(KEY_URL, route.getUrl());//传递当前路由
        return intent;
    }


    public static String getKeyUrl(){
        return KEY_URL;
    }

    public Queue<HistoryItem> getRouteHistories(){
        return mHistoryCaches;
    }

}
