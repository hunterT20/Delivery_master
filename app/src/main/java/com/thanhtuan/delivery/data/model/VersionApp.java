package com.thanhtuan.delivery.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionApp {
    @SerializedName("VersionId")
    @Expose
    private String versionId;
    @SerializedName("VersionNo")
    @Expose
    private String versionNo;
    @SerializedName("LinkUpdate")
    @Expose
    private String linkUpdate;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getLinkUpdate() {
        return linkUpdate;
    }

    public void setLinkUpdate(String linkUpdate) {
        this.linkUpdate = linkUpdate;
    }
}
