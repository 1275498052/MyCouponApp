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
        //加载根布局rootView，即每个Fragment的根布局，4个Fragment根布局各不相同
        View view = loadRootView(inflater, container);

        //虽然每个Fragment的根布局都不同，但里面都有一个FrameLayout坑用来填充具体内容，并且id都是相同的R.id.base_container，拿到这个container，通过addView将具体的Fragment的内容填充进去
        mBaseContainer = view.findViewById(R.id.base_container);

        //将具体的Fragment布局填充到container中，包括4个内容Fragment，以及success，loading，empty，error四种状态的Fragment
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

    //默认初始化时必须调用，一次性添加四种状态的Fragment，但是根据状态来设置是否显示，在子类中重写setUpState判断状态
    private void loadStatesView(LayoutInflater inflater, ViewGroup container) {
        //成功状态
        mSuccessView = loadSuccessView(inflater, container);
        mBaseContainer.addView(mSuccessView);

        //加载中状态
        mLoadingView = loadLoadingView(inflater, container);
        mBaseContainer.addView(mLoadingView);

        //错误状态
        mErrorView = loadErrorView(inflater, container);
        mBaseContainer.addView(mErrorView);

        //空白状态
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

    //加载成功
    protected View loadSuccessView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        //加载内容
        int id = getResId();
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

    //根据id，添加4种内容Fragment（在loadSuccessView中使用），由于这一系列方法一定会实现，所以设置成abstract，强制要求所有子Fragment重写
    protected abstract int getResId();
}
