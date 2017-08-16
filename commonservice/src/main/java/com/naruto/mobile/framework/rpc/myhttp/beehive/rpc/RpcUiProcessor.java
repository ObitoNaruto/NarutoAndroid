package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import com.naruto.mobile.base.R;
import com.naruto.mobile.base.framework.app.ui.ActivityResponsable;
import com.naruto.mobile.base.widget.APFlowTipView;

/**
 * rpc执行时的ui操作代理
 * 如需要自定义ui相关显示，继承该类
 */
public class RpcUiProcessor {

    // ui processor内与界面相关的 都要使用弱引用，防止短期内存泄露问题
    private WeakReference<ActivityResponsable> activityResRef;

    private WeakReference<Fragment> fragmentRef;

    private String loadingText;

    // 默认文案
    private String netErrorText;

    private String netErrorSubText;

    private String netErrorSlowText;

    private String warnText;

    private String warnSubText;

    private String emptyText;

    private Runnable retryRunnable;

    // flowTipView是懒初始化，因此无内存泄露问题
    private APFlowTipView flowTipView;

    private int flowTipHolderId;
    // TODO fragment的view可能有短期泄露问题
    // flowTipView 占位id
    //private View flowTipHolderView;

    public RpcUiProcessor(ActivityResponsable ar) {
        init(ar);
    }

    public RpcUiProcessor(Fragment f) {
        fragmentRef = new WeakReference<Fragment>(f);
        Activity a = f.getActivity();
        if (a != null && a instanceof ActivityResponsable) {
            init((ActivityResponsable)a);
        } else {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG,
//                    "fragment activity is not ActivityResponsible!");
        }
        //this.flowTipHolderView = f.getView();
    }

    private void init(ActivityResponsable ar) {
        activityResRef = new WeakReference<ActivityResponsable>(ar);
        emptyText = "";
        netErrorText = "";
        warnText = "";
        try {
            Resources res = getActivity().getResources();
            loadingText = res.getString(R.string.loading);
            emptyText = res.getString(R.string.no_data);
            netErrorText = res.getString(R.string.no_network);
            netErrorSlowText = res.getString(R.string.slow_network);
            netErrorSubText = res.getString(R.string.no_network_sub);
            warnText = res.getString(R.string.system_fail);
            warnSubText = res.getString(R.string.system_fail_sub);
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
    }

    /**
     * 显示可取消的进度对话框
     * ar保证在ui线程内运行
     */
    public void showProgressDialog(boolean cancelable,
            DialogInterface.OnCancelListener cancelListener) {
        if (getActivityResponsible() != null && isUiValid()) {
            getActivityResponsible().showProgressDialog(loadingText, cancelable, cancelListener);
        }
    }

    public void dismissProgressDialog() {
        if (getActivityResponsible() != null && isUiValid()) {
            getActivityResponsible().dismissProgressDialog();
        }
    }

    public void showTitleBarLoading() {
//        if (getTitleBar() != null && isUiValid()) {
////            getTitleBar().startProgressBar();
//        }
    }

    public void dismissTitleBarLoading() {
//        if (getTitleBar() != null && isUiValid()) {
////            getTitleBar().stopProgressBar();
//        }
    }

//    private TitleBar getTitleBar() {
//        IRpcUiResponsible res = getRpcUiResponsible();
//        if (res != null && res.getTitleBar() != null) {
//            return getRpcUiResponsible().getTitleBar();
//        } else {
//            Activity act = getActivity();
//            if (act != null && !act.isFinishing() && act.getWindow() != null
//                    && act.getWindow().getDecorView() != null) {
//                // 获取根content view, 递归搜索TitleBar
//                View rootView = act.getWindow().getDecorView().findViewById(android.R.id.content);
//                return findTitleBarFromRootView(rootView);
//            }
//        }
//        return null;
//    }

//    private TitleBar findTitleBarFromRootView(View v) {
//        if (v instanceof TitleBar) {
//            return (TitleBar)v;
//        }
//        if (v instanceof ViewGroup) {
//            ViewGroup vg = (ViewGroup)v;
//            int c = vg.getChildCount();
//            for (int i = 0; i < c; i++) {
//                TitleBar result = findTitleBarFromRootView(vg.getChildAt(i));
//                if (result != null) {
//                    return result;
//                }
//            }
//        }
//        return null;
//    }

    public void setFlowTipHolderId(int id) {
        boolean changed = (flowTipHolderId != id);
        flowTipHolderId = id;
        // 重置flowTipView
        if (changed) {
            flowTipView = null;
        }
    }

    public void showNetworkError() {
        showNetworkError(retryRunnable);
    }

    /**
     * 显示网络错误
     * 自定义文案，次级文案接口
     */
    public void showNetworkError(String text, String subText) {
        text = TextUtils.isEmpty(text) ? netErrorText : text;
        subText = TextUtils.isEmpty(subText) ? netErrorSubText : subText;
        setFlowTipViewParams(APFlowTipView.TYPE_NETWORK_ERROR, text, subText, retryRunnable);
    }

    public void showNetworkError(Runnable r) {
        setFlowTipViewParams(APFlowTipView.TYPE_NETWORK_ERROR, netErrorText, netErrorSubText, r);
    }

    public void showEmptyView() {
        showEmptyView(emptyText);
    }

    public void showEmptyView(String tip) {
        if (TextUtils.isEmpty(tip)) {
            tip = emptyText;
        }
        setFlowTipViewParams(APFlowTipView.TYPE_EMPTY, tip, "", null);
    }

    public void showWarn() {
        showWarn(retryRunnable);
    }

    /**
     * 显示提示错误
     * 自定义文案，次级文案接口
     */
    public void showWarn(String text, String subText) {
        text = TextUtils.isEmpty(text) ? warnText : text;
        subText = TextUtils.isEmpty(subText) ? warnSubText : subText;
        setFlowTipViewParams(APFlowTipView.TYPE_WARNING, text, subText, retryRunnable);
    }

    public void showWarn(Runnable runnable) {
        setFlowTipViewParams(APFlowTipView.TYPE_WARNING, warnText, warnSubText, runnable);
    }

    private void setFlowTipViewParams(int type, String tip, String subTip, final Runnable r) {
        // 界面不存在时 不做任何处理
        if (!isUiValid()) {
            return;
        }
        createFlowTipViewIfNot();
        if (flowTipView != null) {
            flowTipView.resetFlowTipType(type);
            if (!TextUtils.isEmpty(tip)) {
                flowTipView.setTips(tip);
            }
            if (!TextUtils.isEmpty(subTip)) {
                flowTipView.setSubTips(subTip);
            }
            if (r != null) {
                flowTipView.setAction(flowTipView.getActionButton().getText().toString(),
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                r.run();
                            }
                        });
            } else {
                flowTipView.setNoAction();
            }
            View p = ((View) flowTipView.getParent());
            p.setVisibility(View.VISIBLE);
            if (p.getParent() != null) {
                ((View)p.getParent()).setVisibility(View.VISIBLE);
            }
        }
    }

    public String getNetErrorText() {
        return netErrorText;
    }

    public void setNetErrorText(String netErrorText) {
        this.netErrorText = netErrorText;
    }

    public String getLoadingText() {
        return loadingText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    public Runnable getRetryRunnable() {
        return retryRunnable;
    }

    public void setWarnText(String warnText) {
        this.warnText = warnText;
    }

    public String getWarnText() {
        return warnText;
    }

    public void setRetryRunnable(Runnable retryRunnable) {
        this.retryRunnable = retryRunnable;
    }

    /**
     * 创建FlowTipView, 默认是位于标题栏下的局部模式
     * 注意是protected
     */
    private void createFlowTipViewIfNot() {
        if (flowTipView == null && getActivity() != null) {
            flowTipView = createFlowTipView();
        }
    }

    protected APFlowTipView createFlowTipView() {
        View parentView = findFlowTipParentViewById(flowTipHolderId);
        return FlowTipViewFactory.buildFlowTipView(getActivity(), parentView);
    }

    private View findFlowTipParentViewById(int id) {
        View result = null;
        if (id > 0 && getActivity() != null) { // 定义了id
            ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
            if (root != null) {
                View v = root.findViewById(id);
                if (v != null) {
                    result = v;
                }
            }
        } else if (fragmentRef != null && fragmentRef.get() != null) { // 在fragment内初始化
            Fragment f = fragmentRef.get();
            result = f.getView();
        } // activity默认parentView为空
        return result;
    }

    public APFlowTipView getFlowTipView() {
        return flowTipView;
    }

    public void hideFlowTipViewIfShow() {
        if (isUiValid() && flowTipView != null) {
            View p = ((View) flowTipView.getParent());
            p.setVisibility(View.GONE);
            if (p.getParent() != null) {
                ((View)p.getParent()).setVisibility(View.GONE);
            }
        }
    }

    public Activity getActivity() {
        if (getActivityResponsible() instanceof Activity) {
            return (Activity) getActivityResponsible();
        } else {
            return null;
        }
    }

    public IRpcUiResponsible getRpcUiResponsible() {
        if (getActivityResponsible() instanceof IRpcUiResponsible) {
            return (IRpcUiResponsible) getActivityResponsible();
        }
        return null;
    }

    private boolean isUiValid() {
        boolean isActivityUiValid = getActivity() != null && !getActivity().isFinishing();
        if (!isActivityUiValid) {
            return false;
        }
        if (fragmentRef != null && fragmentRef.get() != null) {
            return !fragmentRef.get().isDetached();
        } else {
            return true;
        }
    }

    public String getNetErrorSlowText() {
        return netErrorSlowText;
    }

    public ActivityResponsable getActivityResponsible() {
        if (activityResRef != null) {
            return activityResRef.get();
        }
        return null;
    }

}
