package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagerPresenterImpl implements ICategoryPagerPresenter {

    private Map<Integer, Integer> pagesInfo = new HashMap<>();

    public static final int DEFAULT_PAGE = 1;

    private List<ICategoryPagerCallback> callbacks = new LinkedList<>();
    private Integer mCurrentPage;

    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {

                callback.onLoading();
            }
        }

        //设置页码为后面加载更多做准备
        Integer targetPage = pagesInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(categoryId, targetPage);
        }
        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
//                LogUtils.d(this, "homoPagerResult code -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent body = response.body();
//                LogUtils.d(this, body.toString());
//                LogUtils.d(this,   "result -->" + response.body().getData());
                    //把数据给到UI更新
                    handlerHomePagerContentResult(body, categoryId);
                }else {
//                    LogUtils.d(this, "网络错误 --> " + code);
                    handleNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(this, "onFailure -- >" + t);
                handleNetworkError(categoryId);
            }
        });
    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        //根据分类id去加载id对应的内容
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        String url = UrlUtils.createHomePagerUrl(categoryId, targetPage);
//        LogUtils.d(this, "homePagerUrl -- >" + url);
        Call<HomePagerContent> task = api.getHomePagerContent(url);
        return task;
    }

    private void handleNetworkError(int categoryId) {
        //因为Viewpager会加载两旁的Fragment，所以要确定是当前的页面加载错误才显示加载页面
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onError();
            }
        }
    }

    private void handlerHomePagerContentResult(HomePagerContent body, int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (body.getData() == null || body.getData().size() == 0) {
                    callback.onEmpty();
                }else {
                    callback.onLooperListLoaded(body);
                    callback.onContentLoad(body);
                }
            }
        }
    }

    @Override
    public void loaderMore(int categoryId) {
        //1.获得当前页码
        mCurrentPage = pagesInfo.get(categoryId);
        //2.页码++
        mCurrentPage++;
        pagesInfo.put(categoryId, mCurrentPage);
        //3.加载数据
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage);
        //4.处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                //判断结果
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    handleLoaderResult(result, categoryId);
                }else {
                    LogUtils.d(this, "加载更多失败 --> " + code);
                    handleLoaderMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                //请求失败
                LogUtils.d(CategoryPagerPresenterImpl.this,t.toString());
                handleLoaderMoreError(categoryId);
            }
        });
    }

    private void handleLoaderMoreError(int categoryId) {
        mCurrentPage--;
        pagesInfo.put(categoryId,mCurrentPage);
        for(ICategoryPagerCallback callback : callbacks) {
            if(callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
            }
        }
    }

    private void handleLoaderResult(HomePagerContent result, int categoryId) {
        for(ICategoryPagerCallback callback : callbacks) {
            if(callback.getCategoryId() == categoryId) {
                if(result == null || result.getData().size() == 0) {
                    callback.onLoaderMoreEmpty();
                } else {
                    callback.onLoaderMoreLoaded(result);
                }
            }
        }
    }

    @Override
    public void reLoad(int categoryId) {

    }

    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        //将实现了ICategoryPagerCallback接口的HomePagerFragment保存在一个集合中
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callback) {
        callbacks.remove(callback);
    }
}
