package com.mufeng.library.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mufeng.library.ImagePreview;
import com.mufeng.library.R;
import com.mufeng.library.bean.ImageInfo;
import com.mufeng.library.glide.FileTarget;
import com.mufeng.library.glide.ImageLoader;
import com.mufeng.library.glide.progress.ProgressManager;
import com.mufeng.library.utils.image.ImageUtil;
import com.mufeng.library.utils.ui.PhoneUtil;
import com.mufeng.library.utils.ui.ToastUtil;
import com.mufeng.library.view.helper.FingerDragHelper;
import com.mufeng.library.view.helper.ImageSource;
import com.mufeng.library.view.helper.SubsamplingScaleImageViewDragClose;
import com.mufeng.library.view.photoview.PhotoView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @创建者 田汉林
 * @创建时间 2019/5/16 09:43
 * @描述
 */
public class ImagePreviewFragment extends Fragment {


    public static ImagePreviewFragment getInstance(ImageInfo imageInfo){
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("imageInfo",imageInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    private View rootView;

    private Activity activity;

    private FingerDragHelper fingerDragHelper;
    private SubsamplingScaleImageViewDragClose imageView;
    private PhotoView gifImageView;
    private PhotoView thumbnailImageView;

    private FrameLayout fl_progress;
    private TextView tv_progress;

    private ImageInfo imageInfo;

    private String originPathUrl;
    private String thumbPathUrl;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_image_preview,container,false);
        imageInfo = (ImageInfo) getArguments().getSerializable("imageInfo");
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fingerDragHelper = view.findViewById(R.id.fingerDragHelper);
        imageView = view.findViewById(R.id.image_view);
        gifImageView = view.findViewById(R.id.gif_view);
        thumbnailImageView = view.findViewById(R.id.thumbnailImageView);
        fl_progress = view.findViewById(R.id.fl_progress);
        tv_progress = view.findViewById(R.id.tv_progress);

        imageView.setVisibility(View.GONE);
        gifImageView.setVisibility(View.GONE);
        thumbnailImageView.setVisibility(View.GONE);
        fl_progress.setVisibility(View.GONE);

        originPathUrl = imageInfo.getOriginUrl();
        thumbPathUrl = imageInfo.getThumbnailUrl();

        imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageViewDragClose.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomDuration(ImagePreview.getInstance().getZoomTransitionDuration());
        imageView.setMinScale(ImagePreview.getInstance().getMinScale());
        imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
        imageView.setDoubleTapZoomScale(ImagePreview.getInstance().getMediumScale());

        gifImageView.setZoomTransitionDuration(ImagePreview.getInstance().getZoomTransitionDuration());
        gifImageView.setMinimumScale(ImagePreview.getInstance().getMinScale());
        gifImageView.setMaximumScale(ImagePreview.getInstance().getMaxScale());
        gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //点击事件
        imageView.setOnClickListener(v -> {
            if (ImagePreview.getInstance().isEnableClickClose()) {
                activity.finish();
            }
            if (ImagePreview.getInstance().getBigImageClickListener() != null) {
                ImagePreview.getInstance().getBigImageClickListener().onClick(v, ImagePreview.getInstance().getIndex());
            }
        });

        gifImageView.setOnClickListener(v -> {
            if (ImagePreview.getInstance().isEnableClickClose()) {
                activity.finish();
            }
            if (ImagePreview.getInstance().getBigImageClickListener() != null) {
                ImagePreview.getInstance().getBigImageClickListener().onClick(v, ImagePreview.getInstance().getIndex());
            }
        });

        thumbnailImageView.setOnClickListener(v -> {
            if (ImagePreview.getInstance().isEnableClickClose()) {
                activity.finish();
            }
            if (ImagePreview.getInstance().getBigImageClickListener() != null) {
                ImagePreview.getInstance().getBigImageClickListener().onClick(v, ImagePreview.getInstance().getIndex());
            }
        });

        //长按事件
        imageView.setOnLongClickListener(v -> {
            if (ImagePreview.getInstance().getBigImageLongClickListener() != null) {
                return ImagePreview.getInstance().getBigImageLongClickListener().onLongClick(v, ImagePreview.getInstance().getIndex());
            }
            return false;
        });
        gifImageView.setOnLongClickListener(v -> {
            if (ImagePreview.getInstance().getBigImageLongClickListener() != null) {
                return ImagePreview.getInstance().getBigImageLongClickListener().onLongClick(v, ImagePreview.getInstance().getIndex());
            }
            return false;
        });
        thumbnailImageView.setOnLongClickListener(v -> {
            if (ImagePreview.getInstance().getBigImageLongClickListener() != null) {
                return ImagePreview.getInstance().getBigImageLongClickListener().onLongClick(v, ImagePreview.getInstance().getIndex());
            }
            return false;
        });

        if (ImagePreview.getInstance().isEnableDragClose()) {
            fingerDragHelper.setOnAlphaChangeListener((event, translationY) -> {
                float yAbs = Math.abs(translationY);
                float percent = yAbs / PhoneUtil.getPhoneHei(activity.getApplicationContext());
                float number = 1.0F - percent;

                if (activity instanceof ImagePreviewActivity) {
                    ((ImagePreviewActivity) activity).setAlpha(number);
                }
            });
        }

        //显示加载
        fl_progress.setVisibility(View.VISIBLE);
        tv_progress.setText("");

        //判断原图或高清图缓存是否存在
        File cacheFile = ImageLoader.getGlideCacheFile(activity, originPathUrl);
        if (cacheFile != null && cacheFile.exists()) {
            boolean isCacheIsGif = ImageUtil.isGifImageWithMime(cacheFile.getAbsolutePath());
            String imagePath = cacheFile.getAbsolutePath();
            if (isCacheIsGif) {
                loadGifImageSpec(imagePath, fl_progress);
            } else {
                loadImageSpec(imagePath, fl_progress);
            }
        }else {
            //显示缩略图
            thumbnailImageView.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(thumbPathUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .dontAnimate()
                    .into(thumbnailImageView);

            Glide.with(activity)
                    .downloadOnly()
                    .load(originPathUrl)
                    .listener(new RequestListener<File>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                            loadFailed(fl_progress,e);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                            loadSuccess(resource,fl_progress);
                            return true;
                        }
                    })
                    .into(new FileTarget(){
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                        }
                    });

            ProgressManager.addListener(originPathUrl, (url, isComplete, percentage, bytesRead, totalBytes) -> {
                tv_progress.setText(percentage+"%");
            });
        }

    }

    private void loadFailed(FrameLayout fl_progress, GlideException e) {
        e.printStackTrace();
        fl_progress.setVisibility(View.GONE);
        gifImageView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        thumbnailImageView.setVisibility(View.GONE);

        imageView.setZoomEnabled(false);
        imageView.setImage(ImageSource.resource(ImagePreview.getInstance().getErrorPlaceHolder()));

        String errorMsg = "加载失败";
        if (e != null) {
            errorMsg = errorMsg.concat(":\n").concat(e.getMessage());
        }
        if (errorMsg.length() > 200) {
            errorMsg = errorMsg.substring(0, 199);
        }
        ToastUtil.getInstance()._short(activity.getApplicationContext(), errorMsg);
    }

    private void loadSuccess(File resource, FrameLayout progressBar) {
        String imagePath = resource.getAbsolutePath();
        boolean isCacheIsGif = ImageUtil.isGifImageWithMime(imagePath);
        if (isCacheIsGif) {
            loadGifImageSpec(imagePath,  progressBar);
        } else {
            loadImageSpec(imagePath, progressBar);
        }
    }


    /**
     * 设置原图
     * @param imagePath
     * @param flProgress
     */
    private void loadImageSpec(String imagePath, FrameLayout flProgress) {
        gifImageView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        setImageSpec(imagePath, imageView);
        imageView.setOrientation(SubsamplingScaleImageViewDragClose.ORIENTATION_USE_EXIF);
        ImageSource imageSource = ImageSource.uri(Uri.fromFile(new File(imagePath)));
        if (ImageUtil.isBmpImageWithMime(imagePath)) {
            imageSource.tilingDisabled();
        }
        imageView.setImage(imageSource);

        imageView.setOnImageEventListener(new SubsamplingScaleImageViewDragClose.OnImageEventListener() {
            @Override public void onReady() {
                flProgress.setVisibility(View.GONE);
                thumbnailImageView.setVisibility(View.GONE);
            }

            @Override public void onImageLoaded() {

            }

            @Override public void onPreviewLoadError(Exception e) {

            }

            @Override public void onImageLoadError(Exception e) {

            }

            @Override public void onTileLoadError(Exception e) {

            }

            @Override public void onPreviewReleased() {

            }
        });
    }

    private void setImageSpec(String imagePath, SubsamplingScaleImageViewDragClose imageView) {
        boolean isLongImage = ImageUtil.isLongImage(activity, imagePath);
        if (isLongImage) {
            imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_START);
            imageView.setMinScale(ImageUtil.getLongImageMinScale(activity, imagePath));
            imageView.setMaxScale(ImageUtil.getLongImageMaxScale(activity, imagePath));
            imageView.setDoubleTapZoomScale(ImageUtil.getLongImageMaxScale(activity, imagePath));
        } else {
            boolean isWideImage = ImageUtil.isWideImage(activity, imagePath);
            boolean isSmallImage = ImageUtil.isSmallImage(activity, imagePath);
            if (isWideImage) {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(ImagePreview.getInstance().getMinScale());
                imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
                imageView.setDoubleTapZoomScale(ImageUtil.getWideImageDoubleScale(activity, imagePath));
            } else if (isSmallImage) {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CUSTOM);
                imageView.setMinScale(ImageUtil.getSmallImageMinScale(activity, imagePath));
                imageView.setMaxScale(ImageUtil.getSmallImageMaxScale(activity, imagePath));
                imageView.setDoubleTapZoomScale(ImageUtil.getSmallImageMaxScale(activity, imagePath));
            } else {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(ImagePreview.getInstance().getMinScale());
                imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
                imageView.setDoubleTapZoomScale(ImagePreview.getInstance().getMediumScale());
            }
        }
    }

    /**
     * 设置Gif图片
     * @param imagePath
     * @param flProgress
     */
    private void loadGifImageSpec(String imagePath, FrameLayout flProgress) {
        imageView.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);

        Glide.with(activity)
                .asGif()
                .load(imagePath)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(ImagePreview.getInstance().getErrorPlaceHolder()))
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target,
                                                boolean isFirstResource) {
                        gifImageView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImage(ImageSource.resource(ImagePreview.getInstance().getErrorPlaceHolder()));
                        return false;
                    }

                    @Override public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target,
                                                             DataSource dataSource, boolean isFirstResource) {
                        flProgress.setVisibility(View.GONE);
                        thumbnailImageView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(gifImageView);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ImageLoader.clearMemory(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (gifImageView != null){
                gifImageView.destroyDrawingCache();
                gifImageView.setImageBitmap(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (thumbnailImageView != null){
                thumbnailImageView.destroyDrawingCache();
                thumbnailImageView.setImageBitmap(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (imageView != null) {
                imageView.destroyDrawingCache();
                imageView.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
