package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.bean.HomePagerContent;


public interface ICategoryPagerCallback extends IBaseCallback {
    void onContentLoad(HomePagerContent content);

    int getCategoryId();

    //没有更多内容了
    void onLoaderMoreEmpty();
    //加载更多内容错误
    void onLoaderMoreError();
    //加载成功
    void onLoaderMoreLoaded(HomePagerContent content);
    //轮播图回调
    void onLooperListLoaded(HomePagerContent content);
}
