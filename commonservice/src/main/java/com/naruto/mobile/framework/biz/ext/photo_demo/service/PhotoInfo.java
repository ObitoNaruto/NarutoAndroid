
package com.naruto.mobile.framework.biz.ext.photo_demo.service;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoInfo implements Parcelable {

    /**
     * original photo path
     */
    private String photoPath;

    /**
     * original photo width
     */
    private int photoWidth;

    /**
     * original photo height
     */
    private int photoHeight;
    

    public PhotoInfo(String photoPath) {
        this(photoPath, "", "");
    }

    public PhotoInfo(String photoPath, String leftPhotoText, String rightPhotoText) {
        this.photoPath = photoPath;
        this.photoWidth = 0;
        this.photoHeight = 0;
    }

    public PhotoInfo(Parcel in) {
        this.photoPath = in.readString();
        this.photoWidth = in.readInt();
        this.photoHeight = in.readInt();
    }

    public PhotoInfo(PhotoInfo pi) {
        this.photoPath = pi.photoPath;
        this.photoWidth = pi.photoWidth;
        this.photoHeight = pi.photoHeight;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoWidth() {
        return photoWidth;
    }

    public void setPhotoWidth(int photoWidth) {
        this.photoWidth = photoWidth;
    }

    public int getPhotoHeight() {
        return photoHeight;
    }

    public void setPhotoHeight(int photoHeight) {
        this.photoHeight = photoHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoPath);
        dest.writeInt(photoWidth);
        dest.writeInt(photoHeight);
    }

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {

        public PhotoInfo createFromParcel(Parcel in) {
            return new PhotoInfo(in);
        }

        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };

}
