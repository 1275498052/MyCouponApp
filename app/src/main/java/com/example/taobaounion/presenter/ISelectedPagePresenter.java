package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.view.ISelectedPageCallback;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {

    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 根据分类获取内容
     *
     * @param item
     */
    void getContentByCategory(Categories.DataDTO item);

    /**
     * 重新加载内容
     */
    void reloadContent();

}
