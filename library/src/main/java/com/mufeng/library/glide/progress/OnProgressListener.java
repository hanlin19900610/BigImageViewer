package com.mufeng.library.glide.progress;

/**
 * @创建者 by sunfusheng on 2017/6/14.
 * @创建时间 2019/5/15 19:08
 * @描述
 */
public interface OnProgressListener {

    void onProgress(String url, boolean isComplete, int percentage, long bytesRead, long totalBytes);

}
