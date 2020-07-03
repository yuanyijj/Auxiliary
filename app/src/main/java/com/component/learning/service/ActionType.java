package com.component.learning.service;

public enum ActionType {
    Back(-1, "返回"),
    Home_Refresh(0, "刷新"),
    Home_Video(1, "百灵——视频"),
    Home_Television(2, "电视台——长视频"),
    Home_Article(3, "电台——文章"),
    Video_play(4, "视频——播放"),
    Article_collection(5, "文章——收藏"),
    Article_share(6, "文章——分享"),
    Article_share_secondary(7, "文章——分享——点击分享"),
    Article_share_secondary_Back(8, "文章——分享——点击分享—返回"),
    Query_score(9, "文章——分享——点击分享—返回"),

    Video_Back(10, "短视频返回"),
    Television_Video_Back(11, "长视频返回"),
    Article_Back(12, "文章返回");


    private int status;
    private String desc;

    ActionType(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
