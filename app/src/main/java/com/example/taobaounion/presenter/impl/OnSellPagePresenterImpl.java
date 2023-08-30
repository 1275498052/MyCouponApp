package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.bean.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.IOnSellPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {
    private IOnSellPageCallback mView;
    private int mDefaultPage = 1;
    private int mCurrentPage = mDefaultPage;
    private Api mApi;

    @Override
    public void getOnSellContent() {
        if (mView != null) {
            mView.onLoading();
        }
        String url = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = getOnSellContentCall(url);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK && mView != null) {
                    onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                handleError();
            }
        });
    }

    private void onSuccess(OnSellContent body) {
        if (mView != null) {
            try {
                if (body.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size() == 0) {
                    mView.onEmpty();
                } else {
                    mView.onContentLoadedSuccess(body);
                }
            } catch (Exception e) {
                mView.onError();
            }
        }
    }

    private void handleError() {
        if (mView != null) {
            mView.onError();
        }
    }

    private Call<OnSellContent> getOnSellContentCall(String url) {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
        Call<OnSellContent> task = mApi.getOnSellPageContent(url);
        return task;
    }

    @Override
    public void reLoad() {
        getOnSellContent();
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        String url = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellPageContent(url);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    onMoreLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                handleLoadMoreError();
            }
        });
    }

    private void onMoreLoad(OnSellContent body) {
        if (mView != null) {
            try {
                if (body.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size() == 0) {
                    mView.onMoreLoadedEmpty();
                    mCurrentPage--;
                } else {
                    mView.onMoreLoaded(body);
                }
            } catch (Exception e) {
                handleLoadMoreError();
            }
        }
    }

    public void handleLoadMoreError() {
        if (mView != null) {
            mCurrentPage--;
            mView.onMoreLoadedError();
        }
    }

    @Override
    public void registerViewCallback(IOnSellPageCallback callback) {
        this.mView = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellPageCallback callback) {
        mView = null;
    }
}
