package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.bean.TicketParams;
import com.example.taobaounion.model.bean.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    private TicketResult mTicketResult;
    private ITicketPagerCallback mViewCallback;
    private String cover;

    enum LoadState {
        LOADING, SUCCESS, ERROR, NONE
    }

    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {
        this.onTicketLoading();
        this.cover = cover;
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        //防止传入的url有误
        String targetUrl = UrlUtils.getCoverPath(url);
        TicketParams ticketParams = new TicketParams(targetUrl, title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
//                LogUtils.d(TicketPresenterImpl.this, "result code == > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    mTicketResult = response.body();
//                    LogUtils.d(TicketPresenterImpl.this, "result " + mTicketResult);
                    //通知UI更新
                    onTicketLoadedSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                onLoadedTicketError();
            }
        });
    }

    private void onLoadedTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        } else {
            mCurrentState = LoadState.ERROR;
        }
    }

    private void onTicketLoadedSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(cover, mTicketResult);
        } else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        } else {
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = callback;
        if (mCurrentState == LoadState.LOADING) {
            mViewCallback.onLoading();
        } else if (mCurrentState == LoadState.SUCCESS) {
            onTicketLoadedSuccess();
        } else {
            mViewCallback.onError();
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = null;
    }
}
