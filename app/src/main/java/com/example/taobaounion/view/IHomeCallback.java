package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.bean.Categories;

/**
 * 定义View的动作
 *
 */
public interface IHomeCallback extends IBaseCallback {
    void onCategoriesLoad(Categories categories);
}
