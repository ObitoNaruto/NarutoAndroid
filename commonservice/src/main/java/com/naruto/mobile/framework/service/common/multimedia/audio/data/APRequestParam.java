package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * Created by jinmin on 15/4/2.
 */
public class APRequestParam {
    private String ACL;
    private String UID;

    public APRequestParam(String ACL, String UID) {
        this.ACL = ACL;
        this.UID = UID;
    }

    public String getACL() {
        return ACL;
    }

    public void setACL(String ACL) {
        this.ACL = ACL;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @Override
    public String toString() {
        return "APRequestParam{" +
                "ACL='" + ACL + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
