package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.view.IHomeCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {

    private IHomeCallback mIHomeCallback;

    @Override
    public void getCategories() {
        //加载中状态
        if (mIHomeCallback != null) {
            mIHomeCallback.onLoading();
        }
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //成功后返回的结果
                int code = response.code();
//                LogUtils.d(this, "result code -- >" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    Categories categories = response.body();
                    //打出分类信息
//                    LogUtils.d(this,categories.toString());

                    if (mIHomeCallback != null) {
                        if (categories == null || categories.getData().size() == 0) {
                            mIHomeCallback.onEmpty();
                        }else {
                            mIHomeCallback.onCategoriesLoad(categories);
                        }
                    }
//                    LogUtils.d(this, categories.toString());
                }else {
                    if (mIHomeCallback != null) {
                        mIHomeCallback.onError();
                    }
                    LogUtils.i(this, "请求失败。。。");
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                if (mIHomeCallback != null) {
                    mIHomeCallback.onError();
                }
                //失败返回的结果
                LogUtils.e(this, "Categories请求失败。。。");
                LogUtils.e(this,t.toString());
            }
        });
    }

    //注册UI的通知接口
    @Override
    public void registerViewCallback(IHomeCallback callback) {
        mIHomeCallback = callback;
    }
    //取消注册UI
    @Override
    public void unregisterViewCallback(IHomeCallback callback) {
        mIHomeCallback = null;
    }
}
