
package com.naruto.mobile.h5container.api;

public interface H5Plugin extends H5IntentTarget {

    public static final String CLEAR_ALL_COOKIES = "clearAllCookies";
    public static final String GET_MTOP_TOKEN = "getMtopToken";
    public static final String ADD_EVENT_CAL = "addEventCal";
    public static final String VIBRATE = "vibrate";
    public static final String WATCH_SHAKE = "watchShake";
    public static final String IS_INSTALLED_APP = "isInstalledApp";
    public static final String SET_CLIPBOARD = "setClipboard";
    public static final String GET_CLIPBOARD = "getClipboard";
    public static final String SET_SHARE_DATA = "setSharedData";
    public static final String GET_SHARE_DATA = "getSharedData";
    public static final String REMOVE_SHARE_DATA = "removeSharedData";
    public static final String SET_SESSION_DATA = "setSessionData";
    public static final String GET_SESSION_DATA = "getSessionData";
    public static final String EXIT_SESSION = "exitSession";
    public static final String POP_WINDOW = "popWindow";
    public static final String CLOSE_WEBVIEW = "closeWebview";
    public static final String POP_TO = "popTo";
    public static final String PUSH_WINDOW = "pushWindow";
    public static final String GET_NETWORK_TYPE = "getNetworkType";
    public static final String SEND_SMS = "sendSMS";
    public static final String CHECK_JS_API = "checkJSAPI";
    public static final String OPEN_IN_BROWSER = "openInBrowser";
    public static final String SHOW_ALERT = "showAlert";
    public static final String ALERT = "alert";
    public static final String TOAST = "toast";
    public static final String ACTION_SHEET = "actionSheet";
    public static final String RSA = "rsa";
    public static final String CONFIRM = "confirm";
    public static final String START_PACKAGE = "startPackage";
    public static final String SET_TITLE = "setTitle";
    public static final String READ_TITLE = "readTitle";
    public static final String SHOW_TITLE_BAR = "showTitlebar";
    public static final String HIDE_TITLE_BAR = "hideTitlebar";
    public static final String SET_OPTION_MENU = "setOptionMenu";
    public static final String SHOW_OPTION_MENU = "showOptionMenu";
    public static final String HIDE_OPTION_MENU = "hideOptionMenu";
    public static final String SHOW_TOOL_BAR = "showToolbar";
    public static final String SET_TOOL_MENU = "setToolbarMenu";
    public static final String HIDE_TOOL_BAR = "hideToolbar";
    public static final String SHOW_LOADING = "showLoading";
    public static final String HIDE_LOADING = "hideLoading";
    public static final String SHOW_PROGRESS_BAR = "showProgressBar";
    public static final String PULL_REFRESH = "pullRefresh";
    public static final String CAN_PULL_DOWN = "canPullDown";
    public static final String SET_PROXY = "setProxy";
    public static final String KEY_BOARD_BECOME_VISIBLE = "keyboardBecomeVisible";

    public static final String H5_PAGE_SHOULD_LOAD_URL = "h5PageShouldLoadUrl";
    public static final String H5_PAGE_SHOULD_LOAD_DATA = "h5PageShouldLoadData";
    public static final String H5_PAGE_STARTED = "h5PageStarted";
    public static final String H5_PAGE_PROGRESS = "h5PageProgress";
    public static final String H5_PAGE_UPDATED = "h5PageUpdated";
    public static final String H5_PAGE_ERROR = "h5PageError";
    public static final String H5_PAGE_FINISHED = "h5PageFinished";
    public static final String H5_PAGE_CLOSE = "h5PageClose";
    public static final String H5_PAGE_CLOSED = "h5PageClosed";
    public static final String H5_PAGE_LOAD_URL = "h5PageLoadUrl";
    public static final String H5_PAGE_LOAD_DATA = "h5PageLoadData";
    public static final String H5_PAGE_RELOAD = "h5PageReload";
    public static final String H5_PAGE_BACK = "h5PageBack";
    public static final String H5_PAGE_DEV = "h5PageDev";
    public static final String H5_PAGE_FONT_SIZE = "h5PageFontSize";
    public static final String H5_PAGE_RESUME = "h5PageResume";
    public static final String H5_PAGE_RECEIVED_TITLE = "h5PageReceivedTitle";
    public static final String H5_PAGE_BACK_BEHAVIOR = "h5PageBackBehavior";
    public static final String H5_PAGE_JS_CALL = "h5PageJsCall";
    public static final String H5_PAGE_JS_PARAM = "h5PageJsParam";
    public static final String H5_PAGE_SHOW_CLOSE = "h5PageShowClose";
    public static final String H5_PAGE_BACKGROUND = "h5PageBackground";

    public static final String H5_PAGE_PHYSICAL_BACK = "h5PagePhysicalBack";

    public static final String H5_UPDATE_FONT_SIZE = "h5UpdateFontSize";

    public static final String H5_TOOLBAR_BACK = "h5ToolbarBack";
    public static final String H5_TOOLBAR_CLOSE = "h5ToolbarClose";
    public static final String H5_TOOLBAR_MENU = "h5ToolbarMenu";
    public static final String H5_TOOLBAR_MENU_BT = "h5ToolbarMenuBt";
    public static final String H5_TOOLBAR_RELOAD = "h5ToolbarReload";

    public static final String H5_TITLEBAR_TITLE = "titleClick";
    public static final String H5_TITLEBAR_SUBTITLE = "subtitleClick";
    public static final String H5_TITLEBAR_OPTIONS = "optionMenu";

    public static final String H5_PRELOAD = "h5Preload";
    public static final String H5_PRELOAD_INTERRUPT = "h5PreloadInterrupt";
    public static final String H5_PRELOAD_MAX_FLOW = "h5PreloadMaxFlow";

    public static final String H5_START_DOWNLOAD = "startDownload";
    public static final String H5_STOP_DOWNLOAD = "stopDownload";
    public static final String H5_GET_DOWNLOAD_INFO = "getDownloadInfo";
    public static final String H5_URL_VERIFY_RESULT = "urlVerifyResult";
    public static final String H5_SYNC_GLOBAL_BLACKLIST = "syncH5GlobalBlackList";
    public static final String H5_SHOW_TIPS = "showTips";

    public void getFilter(H5IntentFilter filter);

}
