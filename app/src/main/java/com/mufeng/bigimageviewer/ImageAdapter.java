package com.mufeng.bigimageviewer;

import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mufeng.library.bean.ImageInfo;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @创建者 田汉林
 * @创建时间 2019/5/13 15:18
 * @描述
 */
public class ImageAdapter extends BaseQuickAdapter<ImageInfo, BaseViewHolder> {

    public ImageAdapter(@Nullable List<ImageInfo> data) {
        super(R.layout.item_image, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageInfo item) {

        ImageView iv_img = helper.getView(R.id.iv_img);

        Glide.with(mContext)
                .load(item.getThumbnailUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .dontAnimate()
                .override(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenWidth())
                .into(iv_img);


    }
}
