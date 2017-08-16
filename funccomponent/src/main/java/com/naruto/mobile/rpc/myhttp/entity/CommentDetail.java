package com.naruto.mobile.rpc.myhttp.entity;

import java.io.Serializable;
import java.util.List;

public class CommentDetail implements Serializable {

    /**
     * 评价昵称
     */
    public String commentNickName;

    /**
     * uid
     */
    public String commentUserId;

    /**
     * 头像
     */
    public String commentUserImg;

    /**
     * 图片列表
     */
    public List<String> commentImgs;

    /**
     * 评分
     */
    public int commentScore;

    /**
     * 评价id
     */
    public String commentId;

    /**
     * 评价时间
     */
    public String commentTime;


}
