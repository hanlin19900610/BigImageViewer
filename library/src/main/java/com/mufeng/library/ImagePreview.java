package com.mufeng.library;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mufeng.library.bean.ImageInfo;
import com.mufeng.library.view.ImagePreviewActivity;
import com.mufeng.library.view.listener.OnBigImageClickListener;
import com.mufeng.library.view.listener.OnBigImageLongClickListener;
import com.mufeng.library.view.listener.OnBigImagePageChangeListener;
import com.mufeng.library.view.listener.OnDownloadPictureClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * @author 工藤
 * @email gougou@16fan.com
 * cc.shinichi.library
 * create at 2018/5/22  09:06
 * description:
 */
public class ImagePreview {

    // 触发双击的最短时间，小于这个时间的直接返回
    private static final int MIN_DOUBLE_CLICK_TIME = 1500;

    private WeakReference<Context> contextWeakReference;
    private List<ImageInfo> imageInfoList;// 图片数据集合
    private int index = 0;// 默认显示第几个
    private String folderName = "Download";// 下载到的文件夹名（根目录中）

    private float minScale = 0.8f;// 最小缩放倍数
    private float mediumScale = 3.0f;// 中等缩放倍数
    private float maxScale = 5.0f;// 最大缩放倍数

    private boolean isShowIndicator = true;// 是否显示图片指示器（1/9）
    private boolean isShowCloseButton = false;// 是否显示关闭页面按钮
    private boolean isShowDownButton = true;// 是否显示下载按钮
    private int zoomTransitionDuration = 200;// 动画持续时间 单位毫秒 ms

    private boolean isEnableDragClose = false;// 是否启用下拉关闭，默认不启用
    private boolean isEnableClickClose = true;// 是否启用点击关闭，默认启用

    // 关闭按钮图标
    @DrawableRes
    private int closeIconResId = R.drawable.ic_action_close;

    // 下载按钮图标
    @DrawableRes
    private int downIconResId = R.drawable.icon_download_new;

    // 加载失败时的占位图
    @DrawableRes
    private int errorPlaceHolder = R.drawable.load_failed;

    // 点击和长按事件接口
    private OnBigImageClickListener bigImageClickListener;
    private OnBigImageLongClickListener bigImageLongClickListener;
    private OnBigImagePageChangeListener bigImagePageChangeListener;
    private OnDownloadPictureClickListener downloadPictureClickListener;

    // 防止多次快速点击，记录上次打开的时间戳
    private long lastClickTime = 0;

    //自定义Activity布局
    private int activityLayoutRes = -1;

    public static ImagePreview getInstance() {
        return InnerClass.instance;
    }

    public ImagePreview setContext(@NonNull Context context) {
        this.contextWeakReference = new WeakReference<>(context);
        return this;
    }

    public int getActivityLayoutRes() {
        return activityLayoutRes;
    }

    /**
     * 设置Activity界面布局ID
     * @param activityLayoutRes
     * @return
     */
    public ImagePreview setActivityLayoutRes(int activityLayoutRes) {
        this.activityLayoutRes = activityLayoutRes;
        return this;
    }

    public List<ImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    /**
     * 设置图片源
     * @param imageInfoList
     * @return
     */
    public ImagePreview setImageInfoList(@NonNull List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
        return this;
    }

    /**
     * 设置图片源
     * @param imageList
     * @return
     */
    public ImagePreview setImageList(@NonNull List<String> imageList) {
        ImageInfo imageInfo;
        this.imageInfoList = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(imageList.get(i));
            imageInfo.setOriginUrl(imageList.get(i));
            this.imageInfoList.add(imageInfo);
        }
        return this;
    }

    /**
     * 设置单张图片
     * @param image
     * @return
     */
    public ImagePreview setImage(@NonNull String image) {
        this.imageInfoList = new ArrayList<>();
        ImageInfo imageInfo;
        imageInfo = new ImageInfo();
        imageInfo.setThumbnailUrl(image);
        imageInfo.setOriginUrl(image);
        this.imageInfoList.add(imageInfo);
        return this;
    }

    public int getIndex() {
        return index;
    }

    /**
     * 设置图片索引
     * @param index
     * @return
     */
    public ImagePreview setIndex(int index) {
        this.index = index;
        return this;
    }

    public boolean isShowDownButton() {
        return isShowDownButton;
    }

    /**
     * 是否显示下载按钮
     * @param showDownButton
     * @return
     */
    public ImagePreview setShowDownButton(boolean showDownButton) {
        isShowDownButton = showDownButton;
        return this;
    }

    public boolean isShowCloseButton() {
        return isShowCloseButton;
    }

    /**
     * 是否显示关闭按钮
     * @param showCloseButton
     * @return
     */
    public ImagePreview setShowCloseButton(boolean showCloseButton) {
        isShowCloseButton = showCloseButton;
        return this;
    }

    public String getFolderName() {
        if (TextUtils.isEmpty(folderName)) {
            folderName = "Download";
        }
        return folderName;
    }

    /**
     * 设置缓存路径
     * @param folderName
     * @return
     */
    public ImagePreview setFolderName(@NonNull String folderName) {
        this.folderName = folderName;
        return this;
    }

    /**
     * 设置缩放等级
     * @param min
     * @param medium
     * @param max
     * @return
     */
    public ImagePreview setScaleLevel(int min, int medium, int max) {
        if (max > medium && medium > min && min > 0) {
            this.minScale = min;
            this.mediumScale = medium;
            this.maxScale = max;
        } else {
            throw new IllegalArgumentException("max must greater to medium, medium must greater to min!");
        }
        return this;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getMediumScale() {
        return mediumScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public int getZoomTransitionDuration() {
        return zoomTransitionDuration;
    }

    public ImagePreview setZoomTransitionDuration(int zoomTransitionDuration) {
        if (zoomTransitionDuration < 0) {
            throw new IllegalArgumentException("zoomTransitionDuration must greater 0");
        }
        this.zoomTransitionDuration = zoomTransitionDuration;
        return this;
    }

    public boolean isEnableDragClose() {
        return isEnableDragClose;
    }

    /**
     * 是否允许拖拽关闭
     * @param enableDragClose
     * @return
     */
    public ImagePreview setEnableDragClose(boolean enableDragClose) {
        isEnableDragClose = enableDragClose;
        return this;
    }

    public boolean isEnableClickClose() {
        return isEnableClickClose;
    }

    /**
     * 是否允许点击关闭
     * @param enableClickClose
     * @return
     */
    public ImagePreview setEnableClickClose(boolean enableClickClose) {
        isEnableClickClose = enableClickClose;
        return this;
    }

    public int getCloseIconResId() {
        return closeIconResId;
    }

    /**
     * 设置关闭按钮图片资源
     * @param closeIconResId
     * @return
     */
    public ImagePreview setCloseIconResId(@DrawableRes int closeIconResId) {
        this.closeIconResId = closeIconResId;
        return this;
    }

    public int getDownIconResId() {
        return downIconResId;
    }

    /**
     * 设置下载图片资源
     * @param downIconResId
     * @return
     */
    public ImagePreview setDownIconResId(@DrawableRes int downIconResId) {
        this.downIconResId = downIconResId;
        return this;
    }

    public boolean isShowIndicator() {
        return isShowIndicator;
    }

    /**
     * 是否显示图片索引
     * @param showIndicator
     * @return
     */
    public ImagePreview setShowIndicator(boolean showIndicator) {
        isShowIndicator = showIndicator;
        return this;
    }

    public int getErrorPlaceHolder() {
        return errorPlaceHolder;
    }

    /**
     * 设置错误占位图
     * @param errorPlaceHolderResId
     * @return
     */
    public ImagePreview setErrorPlaceHolder(int errorPlaceHolderResId) {
        this.errorPlaceHolder = errorPlaceHolderResId;
        return this;
    }

    public OnBigImageClickListener getBigImageClickListener() {
        return bigImageClickListener;
    }

    /**
     * 图片点击事件
     * @param bigImageClickListener
     * @return
     */
    public ImagePreview setBigImageClickListener(OnBigImageClickListener bigImageClickListener) {
        this.bigImageClickListener = bigImageClickListener;
        return this;
    }

    public OnBigImageLongClickListener getBigImageLongClickListener() {
        return bigImageLongClickListener;
    }

    /**
     * 设置图片长按事件
     * @param bigImageLongClickListener
     * @return
     */
    public ImagePreview setBigImageLongClickListener(OnBigImageLongClickListener bigImageLongClickListener) {
        this.bigImageLongClickListener = bigImageLongClickListener;
        return this;
    }

    public OnBigImagePageChangeListener getBigImagePageChangeListener() {
        return bigImagePageChangeListener;
    }

    /**
     * 设置图片切换事件
     * @param bigImagePageChangeListener
     * @return
     */
    public ImagePreview setBigImagePageChangeListener(OnBigImagePageChangeListener bigImagePageChangeListener) {
        this.bigImagePageChangeListener = bigImagePageChangeListener;
        return this;
    }

    public OnDownloadPictureClickListener getDownloadPictureClickListener() {
        return downloadPictureClickListener;
    }

    public ImagePreview setDownloadPictureClickListener(OnDownloadPictureClickListener downloadPictureClickListener) {
        this.downloadPictureClickListener = downloadPictureClickListener;
        return this;
    }

    public void reset() {
        imageInfoList = null;
        index = 0;
        minScale = 0.8f;
        mediumScale = 3.0f;
        maxScale = 5.0f;
        zoomTransitionDuration = 200;
        isShowDownButton = true;
        isShowCloseButton = false;
        isEnableDragClose = false;
        isEnableClickClose = true;
        isShowIndicator = true;

        closeIconResId = R.drawable.ic_action_close;
        downIconResId = R.drawable.icon_download_new;
        errorPlaceHolder = R.drawable.load_failed;

        folderName = "Download";
        if (contextWeakReference != null) {
            contextWeakReference.clear();
            contextWeakReference = null;
        }

        bigImageClickListener = null;
        bigImageLongClickListener = null;
        bigImagePageChangeListener = null;
        downloadPictureClickListener = null;

        lastClickTime = 0;
    }


    public void start() {
        if (System.currentTimeMillis() - lastClickTime <= MIN_DOUBLE_CLICK_TIME) {
            Log.e("ImagePreview", "---忽略多次快速点击---");
            return;
        }
        if (contextWeakReference == null) {
            throw new IllegalArgumentException("You must call 'setContext(Context context)' first!");
        }
        Context context = contextWeakReference.get();
        if (context == null) {
            throw new IllegalArgumentException("You must call 'setContext(Context context)' first!");
        }
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("context must be a Activity!");
        }
        if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
            reset();
            return;
        }
        if (imageInfoList == null || imageInfoList.size() == 0) {
            throw new IllegalArgumentException(
                    "Do you forget to call 'setImageInfoList(List<ImageInfo> imageInfoList)' ?");
        }
        if (this.index >= imageInfoList.size()) {
            throw new IllegalArgumentException("index out of range!");
        }
        lastClickTime = System.currentTimeMillis();
        ImagePreviewActivity.activityStart(context);
    }

    public ImagePreview showLoading(){
        ((ImagePreviewActivity)ImagePreviewActivity.activityWeakRef.get()).showLoading();
        return this;
    }

    public ImagePreview hideLoading(){
        ((ImagePreviewActivity)ImagePreviewActivity.activityWeakRef.get()).hideLoading();
        return this;
    }

    private static class InnerClass {
        private static ImagePreview instance = new ImagePreview();
    }

}
