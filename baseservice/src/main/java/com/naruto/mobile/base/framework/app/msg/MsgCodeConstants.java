package com.naruto.mobile.base.framework.app.msg;

/**
 * 框架消息
 * 
 * @author sanping.li@alipay.com
 * 
 */
public interface MsgCodeConstants {
	String FRAMEWORK_INITED = "com.alipay.mobile.framework.INITED";
	String FRAMEWORK_ACTIVITY_CREATE = "com.alipay.mobile.framework.ACTIVITY_CREATE";
	String FRAMEWORK_ACTIVITY_RESUME = "com.alipay.mobile.framework.ACTIVITY_RESUME";
	String FRAMEWORK_ACTIVITY_PAUSE = "com.alipay.mobile.framework.ACTIVITY_PAUSE";
	String FRAMEWORK_ACTIVITY_USERLEAVEHINT = "com.alipay.mobile.framework.USERLEAVEHINT";
	String FRAMEWORK_WINDOW_FOCUS_CHANGED = "com.alipay.mobile.framework.WINDOW_FOCUS_CHANGED";
	String FRAMEWORK_VIEW_CLICK = "com.alipay.mobile.framework.VIEW_CLICK";
	String FRAMEWORK_ACTIVITY_DESTROY = "com.alipay.mobile.framework.ACTIVITY_DESTROY"; 
	String FRAMEWORK_ACTIVITY_START         = "com.alipay.mobile.framework.ACTIVITY_START";
	String FRAMEWORK_ACTIVITY_DATA = "com.alipay.mobile.framework.ACTIVITY_DATA";
	 
	// 客户端启动完成，首页已经启动完成
	String FRAMEWORK_CLIENT_STARTED = "com.alipay.mobile.client.STARTED";

	// 首页页面切换
	String HOMEPAGE_CHANGE = "hompage_change";
	
	
    String FRAMEWORK_INITED_PARAM = "inited_param";
}
