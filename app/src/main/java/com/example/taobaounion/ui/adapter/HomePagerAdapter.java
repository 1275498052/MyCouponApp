package com.example.taobaounion.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.ui.fragment.HomePagerFragment;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {

    List<Categories.DataDTO> mData = new ArrayList<>();

    public HomePagerAdapter(@NonNull FragmentManager fm) {
        //参数作用？？？
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    /**
     * 给TabLayout设置数据
     * @param position
     * @return
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position).getTitle();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //将数据传入HomePagerFragment,将页面构造好并显示
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(mData.get(position));
        return homePagerFragment;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void setCategories(Categories categories) {
        mData.clear();
        List<Categories.DataDTO> data = categories.getData();
        mData.addAll(data);
        notifyDataSetChanged();
    }
}
