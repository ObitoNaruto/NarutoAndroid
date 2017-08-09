
package com.naruto.mobile.h5container.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.api.H5Page;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.env.H5Environment;

public class H5ToolMenu extends H5PopMenu {

    public H5ToolMenu(H5Page page) {
        super(page);
    }

    public void exit() {
        menuList = null;
        popupWindow = null;
    }

    public int size() {
        return menuList.size();
    }

    public void showMenu(View anchor) {
        Context context = anchor.getContext();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setVerticalScrollBarEnabled(true);

        Drawable background = H5Environment.getResources()
                .getDrawable(R.drawable.more_options_bg);
        Rect padding = new Rect();
        background.getPadding(padding);

        int containerWidth = 0;
        int contentHeight = 0;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int index = 0; index < menuList.size(); index++) {
            View item = getItemView(index, container);
            item.measure(widthMeasureSpec, heightMeasureSpec);
            int width = item.getMeasuredWidth();
            containerWidth = containerWidth > width ? containerWidth : width;
            container.addView(item);
            contentHeight = contentHeight + item.getMeasuredHeight();
        }

        contentHeight = contentHeight + padding.top + padding.bottom;

        int width = containerWidth + padding.left + padding.right;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);

        int xOffset = location[0] - ((width - anchor.getWidth()) / 2);
        int yOffset = location[1] - contentHeight;

        popupWindow = new PopupWindow(container, width, height);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setBackgroundDrawable(background);
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xOffset, yOffset);
    }

    private View getItemView(int position, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.h5_popup_menu_item, parent,
                false);
        content.setTag(position);
        content.setOnClickListener(onClickListener);

        MenuItem item = menuList.get(position);
        TextView tvTitle = (TextView) content.findViewById(R.id.tv_title);
        tvTitle.setText(item.name);
        tvTitle.setTag(position);

        ImageView ivIcon = (ImageView) content.findViewById(R.id.iv_icon);
        if (item.icon != null && ivIcon != null) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageDrawable(item.icon);
        }
        return content;
    }

    @Override
    public void initMenu() {
        Resources resources = H5Environment.getResources();
        menuList = new ArrayList<MenuItem>();

        menuList.add(new MenuItem(resources.getString(R.string.menu_font),
                H5Container.MENU_FONT, resources
                        .getDrawable(R.drawable.h5_nav_font), false));
    }
}
