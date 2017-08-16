package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.naruto.mobile.base.R;
import com.naruto.mobile.base.widget.APFlowTipView;

/**
 * 通用报错/wifi提示页面 工厂
 */
public class FlowTipViewFactory {

    private static int titleBarHeight;

    public static APFlowTipView buildFlowTipView(Activity context, View parentView) {
        APFlowTipView result;
        if (parentView == null) { // 为activity创建的flowTipView
            result = buildFlowTipView(context);
            result.setIsSimpleType(false);
            return result;
        }
        if (!(parentView instanceof ViewGroup)) {
//            LoggerFactory.getTraceLogger().warn("RpcRunner",
//                    String.format("FlowTIpViewFactory buildFlowTipView(context, view(%d) "
//                                    + "is null or is not ViewGroup",
//                            parentView.getClass().getName()));
            return null;
        }

        ViewGroup vg = (ViewGroup)parentView;
        result = buildFlowTipView(context, vg, 0);
        result.setIsSimpleType(!checkParentViewIsFull(context, vg));
        return result;
    }

    private static boolean checkParentViewIsFull(Activity context, ViewGroup parentView) {
        int height = context.getWindow().getDecorView().getHeight();
        // TODO parentView是否要measure一次?
        int ph = parentView.getMeasuredHeight();
        boolean v = (ph + getTitleBarHeight(context)) >= height;
//        LoggerFactory.getTraceLogger().info(RpcConstant.TAG,
//                String.format("checkParentViewIsFull=%b: parentHeight=%d,activity height=%d,"
//                        + "titleBarHeight=%d", v, ph, height, getTitleBarHeight(context)));
        if (v) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 为Activity创建flowTipView
     */
    private static APFlowTipView buildFlowTipView(Activity context) {
        ViewGroup vg = getActivityRootView(context);
        return buildFlowTipView(context, vg, getTitleBarHeight(context));
    }

    private static int getTitleBarHeight(Activity context) {
        if (titleBarHeight <= 0) {
            titleBarHeight = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 48,
                    context.getResources().getDisplayMetrics());
        }
        return titleBarHeight;
    }

    private static ViewGroup getActivityRootView(Activity context) {
        return (ViewGroup) context.findViewById(android.R.id.content);
    }

    private static APFlowTipView buildFlowTipView(Activity context, ViewGroup parent, int topMargin) {
        ViewGroup.MarginLayoutParams params;
        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = 0;
            params = lp;
        } else {
            params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }

        params.topMargin = topMargin;
        params.bottomMargin = 0;
        params.leftMargin = 0;
        params.rightMargin = 0;
        return buildFlowTipView(context, parent, params);
    }

    private static APFlowTipView buildFlowTipView(Activity context, ViewGroup parent,
            ViewGroup.LayoutParams lp) {
        ViewGroup flowTipGroup = (ViewGroup) LayoutInflater.from(context).
                inflate(R.layout.rpc_flow_tip_view, null);
        if (flowTipGroup.getParent() != parent) {
            parent.addView(flowTipGroup, lp);
        }
        return (APFlowTipView) flowTipGroup.getChildAt(0);
    }

}
