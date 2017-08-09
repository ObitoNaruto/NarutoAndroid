
package com.naruto.mobile.h5container.api;

public interface H5Param {

    public enum ParamType {
        BOOLEAN, STRING, INT, DOUBLE,
    };

    // startup parameters
    public static final String URL = "u";
    public final static String DEFAULT_TITLE = "dt";
    public static final String SHOW_TITLEBAR = "st";
    public static final String SHOW_TOOLBAR = "sb";
    public static final String SHOW_LOADING = "sl";
    public static final String BACK_BEHAVIOR = "bb";
    public static final String CLOSE_BUTTON_TEXT = "cb";
    public static final String SSO_LOGIN_ENABLE = "le";
    public static final String SAFEPAY_ENABLE = "pe";
    public static final String READ_TITLE = "rt";
    public static final String SAFEPAY_CONTEXT = "sc";
    public static final String ANTI_PHISHING = "ap";
    public static final String CAN_PULL_DOWN = "pd";
    public static final String ENABLE_PROXY = "ep";
    public static final String CLOSE_AFTER_PAY_FINISH = "ca";
    public static final String PULL_REFRESH = "pr";
    public static final String BIZ_SCENARIO = "bz";
    public static final String CCB_PLUGIN = "cp";
    public static final String SAFE_MODE = "sm";
    public static final String SHOW_PROGRESS = "sp";
    public static final String SMART_TOOLBAR = "tb";
    public static final String BACKGROUND_COLOR = "bc";

    public static final String LONG_URL = "url";
    public static final String LONG_DEFAULT_TITLE = "defaultTitle";
    public static final String LONG_SHOW_TITLEBAR = "showTitleBar";
    public static final String LONG_SHOW_TOOLBAR = "showToolBar";
    public static final String LONG_SHOW_LOADING = "showLoading";
    public static final String LONG_BACK_BEHAVIOR = "backBehavior";
    public static final String LONG_CLOSE_BUTTON_TEXT = "closeButtonText";
    public static final String LONG_SSO_LOGIN_ENABLE = "ssoLoginEnabled";
    public static final String LONG_SAFEPAY_ENABLE = "safePayEnabled";
    public static final String LONG_READ_TITLE = "readTitle";
    public static final String LONG_SAFEPAY_CONTEXT = "safePayContext";
    public static final String LONG_ANTI_PHISHING = "antiPhishing";
    public static final String LONG_PULL_REFRESH = "pullRefresh";
    public static final String LONG_BIZ_SCENARIO = "bizScenario";
    public static final String LONG_CCB_PLUGIN = "CCBPlugin";
    public static final String LONG_SAFE_MODE = "safeMode";
    public static final String LONG_SHOW_PROGRESS = "showProgress";
    public static final String LONG_SMART_TOOLBAR = "smartToolBar";
    public static final String LONG_ENABLE_PROXY = "enableProxy";
    public static final String LONG_CAN_PULL_DOWN = "canPullDown";
    public static final String LONG_TOOLBAR_MENU = "toolbarMenu";
    public static final String LONG_BACKGROUND_COLOR = "backgroundColor";

    public static final String SESSION_ID = "sessionId";
    public static final String PUBLIC_ID = "publicId";
}
