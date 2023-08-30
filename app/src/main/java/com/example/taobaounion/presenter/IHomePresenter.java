package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.IHomeCallback;

/**
 * 定义主页需要做的
 */
public interface IHomePresenter extends IBasePresenter<IHomeCallback>{
    void getCategories();
}
