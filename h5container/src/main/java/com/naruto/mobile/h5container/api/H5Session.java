
package com.naruto.mobile.h5container.api;

import java.util.Stack;

public interface H5Session extends H5CoreNode {

    public void setId(String id);

    public String getId();

    public boolean exitSession();

    public boolean addPage(H5Page page);

    public boolean removePage(H5Page page);

    public H5Page getTopPage();

    public Stack<H5Page> getPages();

    public H5Scenario getScenario();

    public void setScenario(H5Scenario scenario);

    public void addListener(H5Listener l);

    public void removeListener(H5Listener l);

}
