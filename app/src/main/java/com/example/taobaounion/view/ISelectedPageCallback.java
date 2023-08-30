package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.model.bean.HomePagerContent;

public interface ISelectedPageCallback extends IBaseCallback {

    /**
     * 分类内容结果
     *
     * @param categories 分类内容
     */
    void onCategoriesLoaded(Categories categories);


    /**
     * 内容
     *
     * @param content
     */
    void onContentLoaded(HomePagerContent content);

}
