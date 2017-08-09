
package com.naruto.mobile.h5container.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

interface H5MeasureListener {
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec);
}

public class H5RelativeLayout extends RelativeLayout {
    private H5MeasureListener listener;

    public H5RelativeLayout(Context context) {
        super(context);
    }

    public H5RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLayoutListener(H5MeasureListener l) {
        this.listener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (listener != null) {
            listener.doMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
