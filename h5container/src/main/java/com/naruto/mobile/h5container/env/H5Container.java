
package com.naruto.mobile.h5container.env;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.naruto.mobile.h5container.api.H5Service;
import com.naruto.mobile.h5container.core.H5Router;
import com.naruto.mobile.h5container.core.H5ServiceImpl;

public class H5Container {

    private static H5Service service;
    private static H5Router messeger;

    public static final String H5_SESSION_POP_PARAM = "h5_session_pop_param";
    public static final String H5_SESSION_RESUME_PARAM = "h5_session_resume_param";

    public static final String FONT_SIZE = "h5_font_size";

    public static final int WEBVIEW_FONT_SIZE_LARGEST = 200;
    public static final int WEBVIEW_FONT_SIZE_LARGER = 150;
    public static final int WEBVIEW_FONT_SIZE_NORMAL = 100;
    public static final int WEBVIEW_FONT_SIZE_SMALLER = 75;
    public static final int WEBVIEW_FONT_SIZE_INVALID = -1;

    public static final String CALL = "call";
    public static final String CALL_BACK = "callback";
    public static final String CLIENT_ID = "clientId";
    public static final String PARAM = "param";
    public static final String FUNC = "func";
    public static final String MSG_TYPE = "msgType";
    public static final String KEEP_CALLBACK = "keepCallback";

    public static final String KEY_YES = "YES";
    public static final String KEY_NO = "NO";
    public static final String KEY_START_URL = "start_up_url";
    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_FORCE = "force";
    public static final String KEY_TIP_CONTENT = "tip_content";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PAGE_UPDATED = "pageUpdated";

    public static final String MENU_FONT = "font";
    public static final String MENU_COPY = "copy";
    public static final String MENU_REFRESH = "refresh";
    public static final String MENU_SHARE = "share";
    public static final String MENU_COMPLAIN = "complain";
    public static final String MENU_TAG = "tag";
    public static final String MENU_NAME = "name";

    public static final String INSTALL_PATH = "installPath";
    public static final String INSTALL_TYPE = "installType";
    public static final String INSTALL_HOST = "installHost";

    public static final String ENABLE_MAPLOCAL = "enableMapLocal";
    public static final String APP_ID = "appId";

    public static final String H5_PAGE_DO_LOAD_URL = "h5PageDoLoadUrl";
    public static final String H5_PAGE_LOAD_RESOURCE = "h5PageLoadResource";
    public static final String H5_PAGE_SET_BACK_TEXT = "h5PageSetBackText";
    public static final String SCAN_APP = "scanApp";

    /** single background thread to execute background tasks. */
    private static ThreadPoolExecutor executor;

    public static H5Service getService() {
        synchronized (H5Container.class) {
            if (service == null) {
                service = new H5ServiceImpl();
            }
        }
        return service;
    }

    public static H5Router getMesseger() {
        synchronized (H5Container.class) {
            if (messeger == null) {
                messeger = new H5Router();
            }
        }
        return messeger;
    }

    private static ThreadFactory createThreadFactory(final String name,
            final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    public static ThreadPoolExecutor getExecutorService() {
        synchronized (H5Container.class) {
            if (executor == null) {
                executor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        createThreadFactory("H5 background executor", true));
            }
        }
        return executor;
    }
}
