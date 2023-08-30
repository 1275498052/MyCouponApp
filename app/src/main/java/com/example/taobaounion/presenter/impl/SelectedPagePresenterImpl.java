package com.example.taobaounion.presenter.impl;

import androidx.annotation.NonNull;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {
    private ISelectedPageCallback mView;
    private final Api mApi;

    public SelectedPagePresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getCategories() {
        mView.onLoading();
        Call<Categories> task = mApi.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    mView.onCategoriesLoaded(response.body());
                }else {
                    handleError();
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                handleError();
            }
        });
    }

    @Override
    public void getContentByCategory(Categories.DataDTO item) {
        int id = item.getId();
        String url = UrlUtils.createHomePagerUrl(id, 0);
        Call<HomePagerContent> task = mApi.getHomePagerContent(url);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    mView.onContentLoaded(response.body());
                }else {
                    handleError();
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                handleError();
            }
        });

    }

    private void handleError() {
        mView.onError();
    }

    @Override
    public void reloadContent() {
        getCategories();
    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mView = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        mView = null;
    }
}
