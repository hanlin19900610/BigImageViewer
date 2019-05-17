package com.mufeng.bigimageviewer;

import android.os.Bundle;

import com.mufeng.library.ImagePreview;
import com.mufeng.library.bean.ImageInfo;
import com.mufeng.library.glide.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<ImageInfo> list;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);
        initView();
    }

    private void initView() {

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            ImageLoader.clearMemory(this);
            ImageLoader.cleanDiskCache(this);
        });

        recyclerView = findViewById(R.id.recycler_view);

        list = initData();

        adapter = new ImageAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(recyclerView);

        adapter.setOnItemClickListener((adapter, view, position) -> {

            ImagePreview.getInstance()
                    .setContext(this)
                    //设置数据源
                    .setImageInfoList(list)
                    //设置索引
                    .setIndex(position)
                    .setEnableClickClose(true)
                    .setEnableDragClose(true)
                    .setShowDownButton(true)
                    .setShowIndicator(true)
                    .start();

        });

    }

    private List<ImageInfo> initData() {
        List<ImageInfo> list = new ArrayList<>();
        ImageInfo info;
        info = new ImageInfo();
        info.setThumbnailUrl("https://b-ssl.duitang.com/uploads/item/201904/22/20190422193309_y44wV.jpeg");
        info.setOriginUrl("https://b-ssl.duitang.com/uploads/item/201904/22/20190422193309_y44wV.jpeg");
        list.add(info);

        info = new ImageInfo();
        info.setThumbnailUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/36cb5531-bf13-45c9-8d5f-439a92c3239e.jpg?Expires=1558593459&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=hTGzKaLgdNPefobr1UklmQMidjw%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_fill%2Cw_1000%2Ch_1000%2Fquality%2Cq_100%2Fformat%2Cwebp");
        info.setOriginUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/36cb5531-bf13-45c9-8d5f-439a92c3239e.jpg?Expires=1558593464&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=bHVK%2BMuh8xpf160%2FCygBizLqxm0%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_lfit%2Cw_3000%2Fquality%2Cq_100%2Fwatermark%2Cimage_emxvZ28ucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfMTQ%2Ct_54%2Cx_5%2Cy_5%2Fformat%2Cwebp");
        list.add(info);

        info = new ImageInfo();
        info.setThumbnailUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/537131c4-8b17-45fd-9c6f-2d0e162cb359.jpg?Expires=1558593459&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=b3rDwK7eEWO1XYSzYQg41BBAwHQ%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_fill%2Cw_1000%2Ch_1000%2Fquality%2Cq_100%2Fformat%2Cwebp");
        info.setOriginUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/537131c4-8b17-45fd-9c6f-2d0e162cb359.jpg?Expires=1558593464&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=GAlnMCL596BITmknscTVvIL0x1A%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_lfit%2Cw_3000%2Fquality%2Cq_100%2Fwatermark%2Cimage_emxvZ28ucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfMTQ%2Ct_54%2Cx_5%2Cy_5%2Fformat%2Cwebp");
        list.add(info);

        info = new ImageInfo();
        info.setThumbnailUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/8d24338c-40f0-45c0-b052-d69908336691.jpg?Expires=1558593459&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=qSUTNa8mF1dQ8%2B12aN%2Ff5mqjSsg%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_fill%2Cw_1000%2Ch_1000%2Fquality%2Cq_100%2Fformat%2Cwebp");
        info.setOriginUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/8d24338c-40f0-45c0-b052-d69908336691.jpg?Expires=1558593464&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=YIw2U32lXheky3Q9JRjLeaGZObE%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_lfit%2Cw_3000%2Fquality%2Cq_100%2Fwatermark%2Cimage_emxvZ28ucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfMTQ%2Ct_54%2Cx_5%2Cy_5%2Fformat%2Cwebp");
        list.add(info);

        info = new ImageInfo();
        info.setThumbnailUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/adefd4c7-8309-4506-acd3-28a9caea35f1.jpg?Expires=1558593459&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=0h5FkfBgsePT1FVxSnp%2B0Pa47z8%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_fill%2Cw_1000%2Ch_1000%2Fquality%2Cq_100%2Fformat%2Cwebp");
        info.setOriginUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/adefd4c7-8309-4506-acd3-28a9caea35f1.jpg?Expires=1558593464&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=mGeW5NLIa0eQnmxiyZsupoG9OZs%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_lfit%2Cw_3000%2Fquality%2Cq_100%2Fwatermark%2Cimage_emxvZ28ucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfMTQ%2Ct_54%2Cx_5%2Cy_5%2Fformat%2Cwebp");
        list.add(info);

        info = new ImageInfo();
        info.setThumbnailUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/4ff47490-cc89-41b6-a95c-efa35215c8b0.jpg?Expires=1558593459&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=kL7%2BPylWHJZTtlp%2BudeNIjdY9xo%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_fill%2Cw_1000%2Ch_1000%2Fquality%2Cq_100%2Fformat%2Cwebp");
        info.setOriginUrl("http://pic1.moodfans.com/Pic/2276/2019-05-16/4ff47490-cc89-41b6-a95c-efa35215c8b0.jpg?Expires=1558593464&OSSAccessKeyId=LTAIkQWa5JbEtpni&Signature=WfZQFkDaAqJLRQeEUEE6MDyhBSA%3D&x-oss-process=image%2Fauto-orient%2C0%2Fresize%2Cm_lfit%2Cw_3000%2Fquality%2Cq_100%2Fwatermark%2Cimage_emxvZ28ucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfMTQ%2Ct_54%2Cx_5%2Cy_5%2Fformat%2Cwebp");
        list.add(info);
        return list;
    }
}
