package com.naruto.mobile.applauncher.core;

import java.util.List;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

import com.naruto.mobile.base.serviceaop.NarutoApplicationContext;

/**
 * Interface definition for a widgetGroup
 */
public interface IWidgetGroup {
	
	/**
	 * 设置上下文
	 * @param context
	 */
	void setContext(NarutoApplicationContext context);
	
	/**
	 * 设置tablauncher
	 * @param activity
	 */
	void setContext(Activity activity);
	
	/**
	 *  获得Group的id
	 * @return
	 */
	String getId();
	
	/**
	 *  设置Group的id
	 * @param id
	 */
	void setId(String id);
	
	/**
	 *  获取Group的Indicator
	 * @return
	 */
	View getIndicator();
	
	/**
	 * 获取红点控件
	 */
	View getBadgeView();
	
	/**
	 *  获得这个Group包含的所有view
	 * @return
	 */
	View getView();
	
	/**
	 *  刷新事件的回调，tab launcher回到前台时触发，对非当前widgetGroup，回调onRefresh()。
	 */
	void onRefresh();
	
	/**
	 *  刷新事件的回调，tab launcher回到前台时触发，对当前widgetGroup，回调onReturn()。
	 */
	void onReturn();	
	
	/**
	 *  重新回到激活状态的回调，点击tab切换到当前widget group时触发。
	 */
	void onResume();
	
	/**
	 *  获得这个Group的所有widget
	 * @return
	 */
	List<IWidget> getAllWidgets();
	
	/**
	 * 销毁时调用
	 */
	void destroy();
	
	/**
	 * 键盘响应
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event);
}
