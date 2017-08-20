package com.naruto.mobile.applauncher.core;

import java.util.List;

public interface IWidgetGroupFactory {
	IWidgetGroup getWidgetGroup(String id);
	List<IWidgetGroup> getAllWidgetGroups();
	List<ClassLoader> getClassloaders() ;
}


