package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.persistence.condition;

/**
 * 存储条件
 * Created by jinmin on 15/4/11.
 */
public class Condition {
    public String field;
    public Object value;
    public String op;
    public String conditionJoiner;



    public class ConditionJoiner {
        public static final String AND = "AND";
        public static final String OR = "OR";
        public static final String EXPR = "";
    }

    public class OP {
        public static final String IN = "IN";
        public static final String EQ = "==";
        public static final String NEQ = "!=";
        public static final String BETWEEN = "BETWEEN";
    }
}
