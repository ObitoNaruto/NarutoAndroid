
package com.naruto.mobile.h5container.api;

public interface H5Service extends H5CoreNode {

    public H5Page createPage(H5Context h5Context, H5Bundle params);

    public boolean startPage(H5Context h5Context, H5Bundle params);

    public boolean exitService();

    public boolean addSession(H5Session session);

    public H5Session getSession(String sessionId);

    public boolean removeSession(String sessionId);

    public H5Session getTopSession();

}
