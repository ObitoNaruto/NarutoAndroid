package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

/**
 * 批量文件打包下载的请求类
 */
public class PackageDownReq {
    private String fileIds;
    private String paths;
    private String name;

    /**
     *
     * @param fileIds 文件ID，多个文件ID 之间用 ｜ 分割
     * @param paths 每个 fileId 对应的压缩路径，每个压缩路径之间用 | 分割
     */
    public PackageDownReq(String fileIds, String paths) {
        this.fileIds = fileIds;
        this.paths = paths;
    }

    /**
     *
     * @param fileIds 文件ID，多个文件ID 之间用 ｜ 分割
     * @param paths 每个 fileId 对应的压缩路径，每个压缩路径之间用 | 分割
     * @param name 打包文件名
     */
    public PackageDownReq(String fileIds, String paths, String name) {
        this.fileIds = fileIds;
        this.paths = paths;
        this.name = name;
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
