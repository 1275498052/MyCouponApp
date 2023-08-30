package com.example.taobaounion.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.model.bean.ILinearItemInfo;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LooperPagerAdapter extends PagerAdapter {
    private List<ILinearItemInfo> mData = new ArrayList<>();
    private OnLooperPageItemClickListener mItemClickListener = null;


    /**
     * 实例化控件
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //getCount得到一个很大的值，可以一直向后滑动，但是数据是有限的，真实的位置通过取余得到
        int realPosition = position % mData.size();
        ILinearItemInfo iLinearItemInfo = mData.get(realPosition);
        //得到父容器的尺寸
        int width = container.getMeasuredWidth();
        int height = container.getMeasuredHeight();
        int coverSize = (width > height ? width : height) / 2;
        String coverUrl = UrlUtils.getCoverPath(iLinearItemInfo.getCover(), coverSize);
//        LogUtils.d(this, coverUrl);
        //动态加载一个imageView
        ImageView imageView = new ImageView(container.getContext());
        //设置imageView的详细参数
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        imageView.setLayoutParams(layoutParams);
        //尺寸类型
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //使用glide加载图片
        Glide.with(container.getContext()).load(coverUrl).into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onLooperItemClick(mData.get(realPosition));
                }
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public int getSize() {
        return mData.size();
    }

    public void setData(List<? extends ILinearItemInfo> looperData) {
        mData.clear();
        //轮播图只添加五个数据
        mData.addAll(looperData.subList(0, 5));
        notifyDataSetChanged();
    }

    public void setOnLooperPageItemClickListener(OnLooperPageItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnLooperPageItemClickListener {
        void onLooperItemClick(IBaseInfo item);
    }
}
