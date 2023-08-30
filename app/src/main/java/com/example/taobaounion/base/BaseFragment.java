package com.example.taobaounion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taobaounion.R;
import com.example.taobaounion.model.bean.ILinearItemInfo;
import com.example.taobaounion.utils.LogUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

    private State currentState = State.NONE;
    private View mSuccessView;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;

    public enum State {
        NONE,LOADING,SUCCESS,ERROR,EMPTY
    }

    private Unbinder mBind;
    private FrameLayout mBaseContainer;

    @OnClick(R.id.network_error_tips)
    public void retry() {
        LogUtils.d(this, "点击了重试");
        onRetryClick();
    }

    protected void onRetryClick() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载根布局
        View view = loadRootView(inflater, container);

        //添加一个空的容器视图
        mBaseContainer = view.findViewById(R.id.base_container);
        //在容器里放入当前界面加载成功的图和未成功的图
        loadStatesView(inflater, container);

        //绑定butterKnife
        mBind = ButterKnife.bind(this, view);

        initView(view);
        initListener();
        initPresenter();
        loadData();

        return view;
    }

    protected void initListener() {
    }

    //根布局
    protected View loadRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_layout, container,false);
    }

    private void loadStatesView(LayoutInflater inflater, ViewGroup container) {
        mSuccessView = loadSuccessView(inflater, container);
        mBaseContainer.addView(mSuccessView);

        mLoadingView = loadLoadingView(inflater, container);
        mBaseContainer.addView(mLoadingView);

        mErrorView = loadErrorView(inflater, container);
        mBaseContainer.addView(mErrorView);

        mEmptyView = loadEmptyView(inflater, container);
        mBaseContainer.addView(mEmptyView);

        setUpState(State.NONE);

    }

    public void setUpState(State state) {
        currentState = state;
        if (currentState == State.SUCCESS) {
            mSuccessView.setVisibility(View.VISIBLE);
        }else {
            mSuccessView.setVisibility(View.GONE);
        }
        if (currentState == State.LOADING) {
            mLoadingView.setVisibility(View.VISIBLE);
        }else {
            mLoadingView.setVisibility(View.GONE);
        }
        if (currentState == State.ERROR) {
            mErrorView.setVisibility(View.VISIBLE);
        }else {
            mErrorView.setVisibility(View.GONE);
        }
        if (currentState == State.EMPTY) {
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
       return inflater.inflate(R.layout.fragment_empty,container,false);
    }

    protected View loadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    /**
     * 作用？？？
     * @param inflater
     * @param container
     * @return
     */
    protected View loadSuccessView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        int id = getRootViewResId();
//        LogUtils.d(this, "successView -->" + id);
        View successView = inflater.inflate(id, container, false);
        return successView;
    }

    protected void initView(View view) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
        release();
    }

    protected  void release() {
        //释放资源
    }

    protected void loadData() {
        //加载数据
    }

    protected void initPresenter() {
        //创建presenter
    }

    //返回当前网络加载部分视图的xml文件Id
    protected abstract int getRootViewResId();
}
