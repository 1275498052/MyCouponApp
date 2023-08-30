package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.bean.Histories;
import com.example.taobaounion.model.bean.SearchRecommend;
import com.example.taobaounion.model.bean.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.utils.JsonCacheUtil;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.view.ISearchPageCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterIml implements ISearchPresenter {

    private final Api mApi;
    private ISearchPageCallback mSearchViewCallback = null;
    private String mCurrentKeyword = null;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterIml() {
        RetrofitManager instance = RetrofitManager.getInstance();
        Retrofit retrofit = instance.getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    public static final int DEFAULT_PAGE = 0;
    /**
     * 搜索的当前页面
     */
    private int mCurrentPage = DEFAULT_PAGE;

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
//                LogUtils.d(SearchPresenterIml.this, "getRecommendWords result code -- > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //处理结果
                    if (mSearchViewCallback != null) {
                        mSearchViewCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void doSearch(String keyword) {
        if(mCurrentKeyword == null || !mCurrentKeyword.equals(keyword)) {
            this.saveHistory(keyword);
            this.mCurrentKeyword = keyword;
        }
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onLoading();
        }
//        LogUtils.d(SearchPresenterIml.class, "SearchResult -- > ");
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
//                LogUtils.d(SearchPresenterIml.class, "SearchResult -- > " + response.code());
                if (HttpURLConnection.HTTP_OK == response.code()) {
                    handleSearchResult(response.body());
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onError();
        }
    }

    private void handleSearchResult(SearchResult result) {
        if (mSearchViewCallback != null) {
            if (isEmpty(result)) {
                mSearchViewCallback.onEmpty();
            } else {
                mSearchViewCallback.onSearchSuccess(result);
            }
        }
    }

    private boolean isEmpty(SearchResult body) {
        try {
            return body == null || body.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void research() {
        if(mCurrentKeyword == null) {
            if(mSearchViewCallback != null) {
                mSearchViewCallback.onEmpty();
            }
        } else {
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        if(mCurrentKeyword == null) {
            if(mSearchViewCallback != null) {
                mSearchViewCallback.onMoreLoadedEmpty();
            }
        } else {
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.doSearch(mCurrentPage,mCurrentKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call,Response<SearchResult> response) {
                int code = response.code();
//                LogUtils.d(SearchPresenterIml.this,"do search result code -- > " + code);
                if(code == HttpURLConnection.HTTP_OK) {
                    handleMoreSearchResult(response.body());
                } else {
                    onLoaderMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call,Throwable t) {
                t.printStackTrace();
                onLoaderMoreError();
            }
        });
    }

    private void handleMoreSearchResult(SearchResult result) {
        if(mSearchViewCallback != null) {
            if(isEmpty(result)) {
                //数据为空
                mCurrentPage--;
                mSearchViewCallback.onMoreLoadedEmpty();
            } else {
                mSearchViewCallback.onMoreLoaded(result);
            }
        }
    }

    private void onLoaderMoreError() {
        mCurrentPage--;
        if(mSearchViewCallback != null) {
            mSearchViewCallback.onMoreLoadedError();
        }
    }

    public static final String KEY_HISTORIES = "key_histories";

    public static final int DEFAULT_HISTORIES_SIZE = 10;
    private int mHistoriesMaxSize = DEFAULT_HISTORIES_SIZE;

    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORIES);
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onHistoriesDeleted();
        }
    }

    private void saveHistory(String history) {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        List<String> historiesList = null;
        //如果有数据就去重
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        if (histories == null) {
            histories = new Histories();
        }
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        historiesList.add(history);
        //控制数量
        if (historiesList.size() > 10) {
            historiesList = historiesList.subList(historiesList.size() - 10, historiesList.size());
        }
        histories.setHistories(historiesList);
        mJsonCacheUtil.saveCache(KEY_HISTORIES, histories);
    }

    @Override
    public void registerViewCallback(ISearchPageCallback callback) {
        this.mSearchViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchPageCallback callback) {
        this.mSearchViewCallback = null;
    }
}
