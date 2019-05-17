package com.mufeng.library.view;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @创建者 田汉林
 * @创建时间 2019/5/16 09:45
 * @描述
 */
public class ImagePreviewAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;

    public ImagePreviewAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> list) {
        super(fm, behavior);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }


}
