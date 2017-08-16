package com.naruto.mobile.base.log.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志工厂
 */
public class LoggerFactory {
    /**
     * 日志列表
     */
    private static List<Logger> LoggerList = new ArrayList<Logger>();

    /**
     * 创建一个日志
     * 
     * @param Tag
     * @return
     */
    public static Logger getLogger(String Tag) {
        Logger logger = new Logger(Tag);
        LoggerList.add(logger);
        return logger;
    }

}
