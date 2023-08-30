package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ICategoryPagerCallback;

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryPagerCallback> {
    //根据分类id获取分类内容
    void getContentByCategoryId(int categoryId);

    void loaderMore(int categoryId);

    void reLoad(int categoryId);
}
