/*
 *generation date:Tue Apr 23 20:50:46 CST 2013
 *tool version:4.0.1
 *template version:4.0.1
 */
package com.naruto.mobile.framework.rpc.myhttp.annotation.ext;

public class BizResult extends Object {

    //Constants

    /** 
    * 结果标示 
    */
    public boolean success;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    /** 
    * 结果码 
    */
    public int resultCode;

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    /** 
    * 结果描述 
    */
    public String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    /** 
    * 服务器系统名(没有错误，尽量不要设置这个字段)
    */
    public String appName;

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return this.appName;
    }

}//end of class def