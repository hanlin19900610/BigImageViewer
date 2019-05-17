package com.mufeng.library.bean;

import java.io.Serializable;

/**
 * @创建者 田汉林
 * @创建时间 2019/5/15 18:48
 * @描述
 * @Link https://github.com/SherlockGougou/BigImageViewPager
 */
public class ImageInfo implements Serializable {

    private String thumbnailUrl;// 缩略图，质量很差
    private String originUrl;// 原图或者高清图

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "thumbnailUrl='" + thumbnailUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
                '}';
    }
}
