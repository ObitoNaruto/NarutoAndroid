package com.naruto.mobile.pullrefresh.simple;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonViewHolder {
    private View mContentView;

    protected CommonViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mContentView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mContentView.setTag(this);
    }

    public static CommonViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int layoutId) {
        context = context == null && parent != null?parent.getContext():context;
        CommonViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new CommonViewHolder(context, parent, layoutId);
        } else {
            viewHolder = (CommonViewHolder)convertView.getTag();
        }

        return viewHolder;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void setTextForTextView(int textViewId, CharSequence text) {
        TextView textView = (TextView)ViewFinder.findViewById(this.mContentView, textViewId);
        if(textView != null) {
            textView.setText(text);
        }

    }

    public void setImageForView(int imageViewId, int drawableId) {
        ImageView imageView = (ImageView)ViewFinder.findViewById(this.mContentView, imageViewId);
        if(imageView != null) {
            imageView.setImageResource(drawableId);
        }

    }

    public void setImageForView(int imageViewId, Bitmap bmp) {
        ImageView imageView = (ImageView)ViewFinder.findViewById(this.mContentView, imageViewId);
        if(imageView != null) {
            imageView.setImageBitmap(bmp);
        }

    }

    public void setCheckForCheckBox(int checkViewId, boolean isCheck) {
        CheckBox checkBox = (CheckBox)ViewFinder.findViewById(this.mContentView, checkViewId);
        if(checkBox != null) {
            checkBox.setChecked(isCheck);
        }

    }

    public void setVisibility(int viewId, int visibility) {
        View view = ViewFinder.findViewById(this.mContentView, viewId);
        if(view != null) {
            view.setVisibility(visibility);
        }

    }

    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = ViewFinder.findViewById(this.mContentView, viewId);
        if(view != null) {
            view.setOnClickListener(listener);
        }

    }

    public void setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = ViewFinder.findViewById(this.mContentView, viewId);
        if(view != null) {
            view.setOnTouchListener(listener);
        }

    }
}
