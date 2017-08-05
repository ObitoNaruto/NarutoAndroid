
package com.naruto.mobile.framework.biz.ext.photo_demo.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoInfo;

public class PhotoItem extends PhotoInfo {

    private boolean selectable;

    public PhotoItem() {
        this(null, false, "", "");
    }

    public PhotoItem(String photoPath) {
        this(photoPath, false, "", "");
    }

    public PhotoItem(String photoPath, boolean isSelected) {
        this(photoPath, isSelected, "", "");
    }

    public PhotoItem(String photoPath, boolean isSelected,
            String leftPhotoText, String rightPhotoText) {
        super(photoPath);
        this.selectable = true;
    }

    public PhotoItem(Parcel in) {
        super(in);
        this.selectable = in.readInt() > 0;
    }

    public PhotoItem(PhotoInfo photoInfo) {
        super(photoInfo);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(selectable ? 1 : 0);
    }

    public static final Parcelable.Creator<PhotoItem> CREATOR = new Parcelable.Creator<PhotoItem>() {

        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    @Override
    public String toString() {
        return "photoPath:" + getPhotoPath() + "\n" + "photoWidth:" + getPhotoWidth() + "\nphotoHeight:"
                + getPhotoHeight() + "\nselectable:" + selectable;
    }
}
