package com.mufeng.library.utils.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mufeng.library.ImagePreview;
import com.mufeng.library.utils.file.FileUtil;
import com.mufeng.library.utils.text.MD5Util;
import com.mufeng.library.utils.ui.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @创建者 田汉林
 * @创建时间 2019/5/17 10:13
 * @描述
 */
public class DownloadPictureUtil {

    public static void downloadPicture(final Context context, final String url) {
        SimpleTarget<File> target = new SimpleTarget<File>() {
            @Override public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }

            @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                ToastUtil.getInstance()._short(context,"保存失败");
            }

            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                final String downloadFolderName = ImagePreview.getInstance().getFolderName();
                final String path = Environment.getExternalStorageDirectory() + "/" + downloadFolderName + "/";
                String name = "";
                try {
                    name = url.substring(url.lastIndexOf("/") + 1);
                    if (name.contains(".")) {
                        name = name.substring(0, name.lastIndexOf("."));
                    }
                    name = MD5Util.md5Encode(name);
                } catch (Exception e) {
                    e.printStackTrace();
                    name = System.currentTimeMillis() + "";
                }
                String mimeType = ImageUtil.getImageTypeWithMime(resource.getAbsolutePath());
                name = name + "." + mimeType;
                FileUtil.createFileByDeleteOldFile(path + name);
                boolean result = FileUtil.copyFile(resource, path, name);
                if (result) {
                    // 其次把文件插入到系统图库
                    try {
                        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                path+name, name, null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    // 最后通知图库更新
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(path + name));
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    ToastUtil.getInstance()._short(context,"已保存到相册");
                } else {
                    ToastUtil.getInstance()._short(context,"保存失败");
                }
            }
        };
        Glide.with(context).downloadOnly().load(url).into(target);
    }

}
