package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.condition;

import java.util.List;

/**
 * Created by jinmin on 15/4/15.
 */
public class OrmliteConditionConvert {
    public static String convert2String(Condition condition) {
        StringBuilder sb = new StringBuilder();
        if (Condition.ConditionJoiner.AND.equals(condition.conditionJoiner)) {
            sb.append(" AND ");
        } else if (Condition.ConditionJoiner.OR.equals(condition.conditionJoiner)) {
            sb.append(" OR ");
        } else if (Condition.ConditionJoiner.EXPR.equals(condition.conditionJoiner)) {
            sb.append(" ");
        }
        sb.append(condition.field).append(getOp(condition.op, condition.value));
        return sb.toString();
    }

    private static String getOp(String op, Object value) {
        if (Condition.OP.EQ.equals(op)) {
            return " = " + value ;
        } else if (Condition.OP.NEQ.equals(op)) {
            return " <> " + value;
        } else if (Condition.OP.IN.equals(op)) {
            if (value instanceof List) {
                List list = (List)value;
                if (list.size() > 0) {
                    StringBuilder sb = new StringBuilder("(");
                    for (int i = 0; i < list.size() - 1; i++) {
                        sb.append(list.get(i)).append(", ");
                    }
                    if (list.size() > 1) {
                        sb.append(list.get(list.size()-1));
                    }
                    sb.append(")");
                    return sb.toString();
                }
            } else if (value.getClass().isArray()) {
                Object[] objects = (Object[])value;
                if (objects.length > 0) {
                    StringBuilder sb = new StringBuilder("(");
                    for (int i = 0; i < objects.length - 1; i++) {
                        sb.append(objects[i]).append(", ");
                    }
                    if (objects.length > 1) {
                        sb.append(objects[objects.length-1]);
                    }
                    sb.append(")");
                    return sb.toString();
                }
            }
        }
        return "";
    }


}
