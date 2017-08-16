package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.model.ResultAction;
import com.naruto.mobile.framework.service.R;
//import com.alipay.mobile.beehive.R;
//import com.alipay.mobile.beehive.rpc.model.ResultAction;
//import com.alipay.mobile.beehive.util.JumpUtil;
//import com.alipay.mobile.common.logging.api.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * rpc结果通用逻辑处理，通过解析showType/followAction处理ui响应
 * Created by zhanqu.awb on 15/7/10.
 */
public class ResultActionProcessor {

    public static final String TYPE_TOAST = "toast";

    public static final String TYPE_ALERT = "alert";

    public static final String TYPE_LINK = "link";

    public static final String TYPE_FINISH_PAGE = "finishPage";

    public static final String TYPE_SHOW_WARN = "showWarn";

    public static final String TYPE_RETRY = "retry";

    public static final String TRIGGER_TYPE_AUTO = "auto";

    public static final String TRIGGER_TYPE_CLICK = "click";

    public static final String TRIGGER_TYPE_MAIN_CLICK = "mainClick";

    public static final String TRIGGER_TYPE_SUB_CLICK = "subClick";

    public static final String DESC = "desc";

    public static final String TITLE = "title";

    public static final String ALERT_MAIN_TEXT = "mainText";

    public static final String ALERT_SUB_TEXT = "subText";

    public static final String LINK_SCHEMA = "schema";

    public static final String RESULT_VIEW = "resultView";

    public static final String SHOW_TYPE = "showType";

    public static final String TRIGGER_TYPE = "triggerType";

    public static final String TRIGGER_ACTIONS = "triggerActions";

    /**
     * 处理老的showType, resultView等
     */
    public static boolean processShowType(RpcUiProcessor rr, Object result) {
        try {
            // 不要处理success=true时的toast
//            Field successField = RpcUtil.getFieldByReflect(result, "success");
//            if (successField != null) {
//                Boolean success = (Boolean) successField.get(result);
//                if (success) {
//                    LoggerFactory.getTraceLogger().info(RpcConstant.TAG,
//                            "do not process showType on success=true");
//                    return;
//                }
//            } else {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "success字段不存在");
//            }

            String resultView;
            Field resultViewField = RpcUtil.getFieldByReflect(result, RESULT_VIEW);
            if (resultViewField != null) {
                resultView = (String) resultViewField.get(result);
                if (TextUtils.isEmpty(resultView)) {
//                    LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "resultView is empty");
                    return false;
                }
            } else {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "resultView字段不存在");
                return false;
            }

            Field showTypeField = RpcUtil.getFieldByReflect(result, SHOW_TYPE);
            if (showTypeField != null) {
                int showType = (Integer) showTypeField.get(result);
//                LoggerFactory.getTraceLogger().info("RpcRunner",
//                        String.format("showType=%d, resultView=%s", showType, resultView));
                if (rr.getActivityResponsible() == null) {
//                    LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, "getActivityResponsible()=null!");
                    return false;
                }
                if (showType == 0) {
                    rr.getActivityResponsible().toast(resultView, Toast.LENGTH_SHORT);
//                    LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "toast resultView success");
                } else if (showType == 1) {
                    rr.getActivityResponsible().alert("", resultView, rr.getActivity().getResources().getString(R.string.confirm), null, "", null);
//                    LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "alert resultView success");
                }
                return true;
            } else {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "showType字段不存在");
            }
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
        return false;
    }

    /**
     * 处理新的action
     */
    public static void processAction(RpcUiProcessor rr, String action) {
        try {
            if (TextUtils.isEmpty(action)) {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "followAction是空串");
                return;
            }
//            LoggerFactory.getTraceLogger().info(RpcConstant.TAG, "followAction=" + action);
            // json串处理
            ResultAction ra = JSON.parseObject(action, new TypeReference<ResultAction>() {
            });
            if (ra != null) {
                processResultAction(rr, ra, TRIGGER_TYPE_AUTO);
            } else {
//                LoggerFactory.getTraceLogger().info(RpcConstant.TAG,
//                        "followAction反解json失败，检查json格式是否正确");
            }
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
    }

    private static void processResultAction(RpcUiProcessor rr, ResultAction action, String triggerType) {
        try {
            String tType = action.triggerType;
            // 如果resultAction未设置triggerType,默认使用自动触发
            if (TextUtils.isEmpty(tType)) {
                tType = TRIGGER_TYPE_AUTO;
            }
            if (!TextUtils.equals(tType, triggerType)) {
                return;
            }
            String type = action.type;
            if (TextUtils.equals(type, TYPE_TOAST)) {
                processToastAction(rr, action);
            } else if (TextUtils.equals(type, TYPE_ALERT)) {
                processAlertAction(rr, action);
            } else if (TextUtils.equals(type, TYPE_LINK)) {
                processLinkAction(rr, action);
            } else if (TextUtils.equals(type, TYPE_FINISH_PAGE)) {
                processFinishPageAction(rr, action);
            } else if (TextUtils.equals(type, TYPE_SHOW_WARN)) {
                processShowWarnAction(rr, action);
            } else if (TextUtils.equals(type, TYPE_RETRY)) {
                processRetryAction(rr, action);
            }
            // 额外处理自动触发的action
            processTriggerAction(rr, action, TRIGGER_TYPE_AUTO);
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
    }

    private static void processToastAction(RpcUiProcessor rr, ResultAction action) {
        Map<String, String> extInfo = action.extInfo;
        if (extInfo != null) {
            String tip = extInfo.get(DESC);
            if (!TextUtils.isEmpty(tip)) {
                rr.getActivityResponsible().toast(tip, Toast.LENGTH_SHORT);
            }
        }
    }

    private static void processAlertAction(final RpcUiProcessor rr, final ResultAction action) {
        Map<String, String> extInfo = action.extInfo;
        if (extInfo != null) {
            String title = extInfo.get(TITLE);
            String desc = extInfo.get(DESC);
            String mainText = extInfo.get(ALERT_MAIN_TEXT);
            if (TextUtils.isEmpty(mainText)) {
                try {
                    mainText = /*rr.getRpcUiResponsible().getTitleBar().getResources().getString(R.string.confirm)*/"";
                } catch (Exception ex) {
//                    LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
                }
            }
            String subText = extInfo.get(ALERT_SUB_TEXT);
            DialogInterface.OnClickListener subClickListener = null;
            if (!TextUtils.isEmpty(subText)) {
                subClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processTriggerAction(rr, action, TRIGGER_TYPE_SUB_CLICK);
                    }
                };
            }

            if (!TextUtils.isEmpty(desc)) {
                rr.getActivityResponsible().alert(title, desc, mainText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processTriggerAction(rr, action, TRIGGER_TYPE_MAIN_CLICK);
                    }
                }, subText, subClickListener);
            }
        }
    }

    private static void processTriggerAction(RpcUiProcessor rr, ResultAction action, String triggerType) {
        List<ResultAction> triggerActions = action.triggerActions;
        if (triggerActions != null) {
            for (ResultAction obj : triggerActions) {
                if (obj != null) {
                    processResultAction(rr, obj, triggerType);
                }
            }
        }
    }

    private static void processLinkAction(RpcUiProcessor rr, ResultAction action) {
        Map<String, String> extInfo = action.extInfo;
        if (extInfo != null) {
            String schema = extInfo.get(LINK_SCHEMA);
            if (!TextUtils.isEmpty(schema)) {
//                JumpUtil.processSchema(schema);
            }
        }
    }

    private static void processFinishPageAction(RpcUiProcessor rr, ResultAction action) {
        try {
            if (rr.getActivityResponsible() instanceof Activity) {
                ((Activity) rr.getActivityResponsible()).finish();
            }
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
    }

    private static void processShowWarnAction(final RpcUiProcessor rr, final ResultAction action) {
        try {
            boolean hasTrigger = action.triggerActions != null && action.triggerActions.size() > 0;
            Map<String, String> extInfo = action.extInfo;
            if (extInfo != null) {
                String desc = extInfo.get(DESC);
                if (!TextUtils.isEmpty(desc)) {
                    rr.setWarnText(desc);
                }
            }
            rr.showWarn(hasTrigger ? new Runnable() {
                @Override
                public void run() {
                    processTriggerAction(rr, action, TRIGGER_TYPE_CLICK);
                }
            } : null);
        } catch (Exception ex) {
//            LoggerFactory.getTraceLogger().warn(RpcConstant.TAG, ex);
        }
    }

    private static void processRetryAction(RpcUiProcessor rr, ResultAction action) {
        if (rr.getRetryRunnable() != null) {
            rr.getRetryRunnable().run();
        }
    }

}
