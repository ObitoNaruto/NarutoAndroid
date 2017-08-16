package com.naruto.mobile.framework.rpc.myhttp.beehive.rpc.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
* rpc返回结果行为 定义
* Created by zhanqu.awb on 15/7/9.
*/
public class ResultAction implements Serializable {

    /*
    1. type目前有以下几种类型，不同的type对应不同的extInfo取值, 同时可取的triggerAction key也不同

    a. toast类型
    extInfo key：
    “desc” : toast文案

    triggerActions可包含：auto触发的动作

    实际使用场景例子：toast后自动跳转

    b. alert类型
    extInfo key:
    “title”:  标题, 可选
    “desc”:  alert内容, 可选
    “mainText”: alert主按钮文案
    “subText”: alert次按钮文案

    triggerActions可包含mainClick, subClick, auto等触发的动作:

    实际使用场景例子：点击主按钮后跳转 | 点击主按钮后关闭当前界面

    c. link类型，跳转动作
    extInfo key:
    “schema”: 跳转目的地schema url, 支持native跳转和url跳转

    triggerActions可包含：auto触发的动作

    实际使用场景例子：跳转到其他界面时同时关闭当前界面

    d. finishPage, 关闭当前界面(activity)的动作
    extInfo 应该为空
    triggerActions 应该为空, 一般关闭界面后不再有其他触发动作

    e. showWarn, 显示提醒页面的动作(一般用在异常情况)
    extInfo key:
    "desc": 提醒说明文案
    triggerAction 应该为空, 一般关闭界面后不需要再有其他触发动作

    f. retry, 重试rpc的动作(一般用在异常情况)
    extInfo 应该为空
    triggerActions 应该为空

    2. triggerType参数表示当前action是由什么触发的，
    目前取值有：auto, mainClick, subClick
    auto: 表示自动触发，默认值， 如果triggerType为空，内部会当作"auto"处理
    mainClick: alert的主按钮点击触发
    subClick: alert的次按钮点击触发
    click: (普通按钮)点击触发
    */

    public String type;

    public String triggerType;

    public Map<String, String> extInfo;

    public List<ResultAction> triggerActions;

}
