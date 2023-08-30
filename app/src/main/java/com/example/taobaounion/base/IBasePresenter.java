package com.example.taobaounion.base;

import com.example.taobaounion.view.ICategoryPagerCallback;

public interface IBasePresenter<T> {

    void registerViewCallback(T callback);

    void unregisterViewCallback(T callback);
}
