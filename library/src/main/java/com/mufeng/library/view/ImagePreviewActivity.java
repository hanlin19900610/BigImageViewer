package com.mufeng.library.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mufeng.library.ImagePreview;
import com.mufeng.library.R;
import com.mufeng.library.bean.ImageInfo;
import com.mufeng.library.glide.ImageLoader;
import com.mufeng.library.glide.progress.ProgressManager;
import com.mufeng.library.utils.file.FileUtil;
import com.mufeng.library.utils.image.DownloadPictureUtil;
import com.mufeng.library.utils.image.ImageUtil;
import com.mufeng.library.utils.ui.ToastUtil;
import com.mufeng.library.view.listener.OnDownloadPictureClickListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * @创建者 田汉林
 * @创建时间 2019/5/16 09:52
 * @描述
 */
public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static void activityStart(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, ImagePreviewActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static final String TAG = "ImagePreview";
    public static WeakReference<Activity> activityWeakRef;

    private Context context;

    private List<ImageInfo> imageInfoList;
    private int currentItem;// 当前显示的图片索引
    private boolean isShowDownButton;
    private boolean isShowCloseButton;
    private boolean isShowIndicator;


    private ImagePreviewAdapter imagePreviewAdapter;
    private HackyViewPager viewPager;
    private TextView tv_indicator;

    private ImageView img_download;
    private ImageView imgCloseButton;
    private ConstraintLayout rootView;

    // 指示器显示状态
    private boolean indicatorStatus = false;
    // 下载按钮显示状态
    private boolean downloadButtonStatus = false;
    // 关闭按钮显示状态
    private boolean closeButtonStatus = false;

    private List<Fragment> fragments;

    private FrameLayout loading_view;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            // 隐藏导航栏
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            // 全屏(隐藏状态栏)
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            // 使用沉浸式必须加这个flag
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                //解决Android O(APi 28)版本以上的刘海屏适配
                WindowManager.LayoutParams windowManagerDu = getWindow().getAttributes();
                windowManagerDu.layoutInDisplayCutoutMode=WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getWindow().setAttributes(windowManagerDu);
            }

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ImagePreview.getInstance().getActivityLayoutRes() == -1) {
            setContentView(R.layout.activity_image_preview);
        } else {
            setContentView(ImagePreview.getInstance().getActivityLayoutRes());
        }

        context = this;
        setBaseActivityWeakRef(this);

        imageInfoList = ImagePreview.getInstance().getImageInfoList();
        if (null == imageInfoList || imageInfoList.size() == 0) {
            onBackPressed();
            return;
        }

        currentItem = ImagePreview.getInstance().getIndex();
        isShowDownButton = ImagePreview.getInstance().isShowDownButton();
        isShowCloseButton = ImagePreview.getInstance().isShowCloseButton();
        isShowIndicator = ImagePreview.getInstance().isShowIndicator();

        rootView = findViewById(R.id.root_view);
        viewPager = findViewById(R.id.view_pager);
        tv_indicator = findViewById(R.id.tv_indicator);
        loading_view = findViewById(R.id.loading_view);
        loading_view.setVisibility(View.GONE);
        img_download = findViewById(R.id.img_download);
        imgCloseButton = findViewById(R.id.imgCloseButton);

        img_download.setImageResource(ImagePreview.getInstance().getDownIconResId());
        imgCloseButton.setImageResource(ImagePreview.getInstance().getCloseIconResId());

        // 关闭页面按钮
        imgCloseButton.setOnClickListener(this);
        // 下载图片按钮
        img_download.setOnClickListener(this);

        if (!isShowIndicator) {
            tv_indicator.setVisibility(View.GONE);
            indicatorStatus = false;
        } else {
            if (imageInfoList.size() > 1) {
                tv_indicator.setVisibility(View.VISIBLE);
                indicatorStatus = true;
            } else {
                tv_indicator.setVisibility(View.GONE);
                indicatorStatus = false;
            }
        }

        if (isShowDownButton) {
            img_download.setVisibility(View.VISIBLE);
            downloadButtonStatus = true;
        } else {
            img_download.setVisibility(View.GONE);
            downloadButtonStatus = false;
        }

        if (isShowCloseButton) {
            imgCloseButton.setVisibility(View.VISIBLE);
            closeButtonStatus = true;
        } else {
            imgCloseButton.setVisibility(View.GONE);
            closeButtonStatus = false;
        }

        // 更新进度指示器
        tv_indicator.setText(
                String.format(getString(R.string.indicator), currentItem + 1 + "", "" + imageInfoList.size()));

        fragments = new ArrayList<>(imageInfoList.size());
        for (ImageInfo imageInfo : imageInfoList) {
            fragments.add(ImagePreviewFragment.getInstance(imageInfo));
        }

        imagePreviewAdapter = new ImagePreviewAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        viewPager.setAdapter(imagePreviewAdapter);
        viewPager.setCurrentItem(currentItem);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (ImagePreview.getInstance().getBigImagePageChangeListener() != null) {
                    ImagePreview.getInstance().getBigImagePageChangeListener().onPageSelected(position);
                }
                currentItem = position;
                ImagePreview.getInstance().setIndex(position);
                // 更新进度指示器
                tv_indicator.setText(
                        String.format(getString(R.string.indicator), currentItem + 1 + "", "" + imageInfoList.size()));
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if (ImagePreview.getInstance().getBigImagePageChangeListener() != null) {
                    ImagePreview.getInstance()
                            .getBigImagePageChangeListener()
                            .onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (ImagePreview.getInstance().getBigImagePageChangeListener() != null) {
                    ImagePreview.getInstance().getBigImagePageChangeListener().onPageScrollStateChanged(state);
                }
            }
        });

        viewPager.setOffscreenPageLimit(imageInfoList.size());
    }



    /**
     * 下载当前图片到SD卡
     */
    private void downloadCurrentImg() {
        OnDownloadPictureClickListener downloadPictureClickListener = ImagePreview.getInstance().getDownloadPictureClickListener();
        if (downloadPictureClickListener == null) {
            showLoading();
            String url = imageInfoList.get(currentItem).getOriginUrl();
            //执行默认下载
            File file = ImageLoader.getGlideCacheFile(context, url);
            if (file != null && file.exists()) {
                //从缓存中复制文件保存更新到相册
                File appDir = new File(Environment.getExternalStorageDirectory(), "BigImageViewer");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String type = ImageUtil.getImageTypeWithMime(file.getAbsolutePath());
                String fileName = System.currentTimeMillis() + "." + type;
                File destFile = new File(appDir, fileName);
                boolean copySuccess = FileUtil.copyFile(file, destFile);
                if (copySuccess) {
                    ImageUtil.saveImageToPhotos(context, destFile.getAbsolutePath());
                    ToastUtil.getInstance()._short(ImagePreviewActivity.this, "已保存到相册");
                }
            } else {
                //如果缓存中没有,则下载保存到本地
                DownloadPictureUtil.downloadPicture(this, url);
            }
            hideLoading();
        } else {
            //执行自定义下载
            downloadPictureClickListener.download(imageInfoList.get(currentItem).getOriginUrl(), currentItem);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public int convertPercentToBlackAlphaColor(float percent) {
        percent = Math.min(1, Math.max(0, percent));
        int intAlpha = (int) (percent * 255);
        String stringAlpha = Integer.toHexString(intAlpha).toLowerCase();
        String color = "#" + (stringAlpha.length() < 2 ? "0" : "") + stringAlpha + "000000";
        return Color.parseColor(color);
    }

    public void setAlpha(float alpha) {
        int colorId = convertPercentToBlackAlphaColor(alpha);
        rootView.setBackgroundColor(colorId);
        if (alpha >= 1) {
            if (indicatorStatus) {
                tv_indicator.setVisibility(View.VISIBLE);
            }
            if (downloadButtonStatus) {
                img_download.setVisibility(View.VISIBLE);
            }
            if (closeButtonStatus) {
                imgCloseButton.setVisibility(View.VISIBLE);
            }
        } else {
            tv_indicator.setVisibility(View.GONE);
            img_download.setVisibility(View.GONE);
            imgCloseButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_download) {// 检查权限
            img_download.setClickable(false);
            if (ActivityCompat.shouldShowRequestPermissionRationale(ImagePreviewActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 拒绝权限
                ToastUtil.getInstance()._short(context, "您拒绝了存储权限，下载失败");
            } else if (ActivityCompat.checkSelfPermission(ImagePreviewActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(ImagePreviewActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            } else {
                downloadCurrentImg();
            }
        } else if (i == R.id.imgCloseButton) {
            onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    downloadCurrentImg();
                } else {
                    ToastUtil.getInstance()._short(context, "您拒绝了存储权限，下载失败");
                }
            }
        }
    }

    public void showLoading() {
        img_download.setClickable(false);
        loading_view.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        img_download.setClickable(true);
        loading_view.setVisibility(View.GONE);
    }


    public static void setBaseActivityWeakRef(Activity activity) {
        activityWeakRef = new WeakReference<>(activity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagePreview.getInstance().reset();
        ProgressManager.removeAll();
    }
}
