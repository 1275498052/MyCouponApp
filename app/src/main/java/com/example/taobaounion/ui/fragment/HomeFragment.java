package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.ui.activity.MainActivity;
import com.example.taobaounion.ui.adapter.HomePagerAdapter;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.view.IHomeCallback;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallback {

    @BindView(R.id.home_indicator)
    public TabLayout mTabLayout;

    @BindView(R.id.home_pager)
    public ViewPager mHomePager;

    @BindView(R.id.scan_icon)
    public ImageView mScan;

    @BindView(R.id.home_search_input_box)
    public View mSearchInputBox;

    private IHomePresenter mHomePresenter;
    private HomePagerAdapter mHomePagerAdapter;

    //设置为successView
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    //通过findViewById的型式绑定控件，对其进行数据操作
    @Override
    protected void initView(View view) {
        //把TabLayout和viewPager联系起来
        mTabLayout.setupWithViewPager(mHomePager);
        //给ViewPager创建适配器,需要传入一个子FragmentManger（在Activity中getFragmentManager）
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        //给viewPager设置适配器，把viewpager和fragment绑定起来
        mHomePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected void initListener() {
        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).switch2Search();
                }
            }
        });
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), ScanQrCodeActivity.class));
            }
        });
    }

    @Override
    protected View loadRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_home_layout, container, false);
    }

    @Override
    protected void loadData() {
        //加载数据
        mHomePresenter.getCategories();
    }

    @Override
    protected void initPresenter() {
        //创建presenter
        mHomePresenter = PresenterManager.getInstance().getHomePresenter();
        mHomePresenter.registerViewCallback(this);
    }

    @Override
    public void onCategoriesLoad(Categories categories) {
        setUpState(State.SUCCESS);
//        LogUtils.d(this, "onCategoriesLoad...");
        //防止Ui销毁，数据依然没有回来
        if (mHomePagerAdapter != null) {
            mHomePagerAdapter.setCategories(categories);
        }
    }

    @Override
    protected void onRetryClick() {//重新加载分类
        if (mHomePresenter != null) {
            mHomePresenter.getCategories();
        }
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    protected void release() {
        //取消回调注册
        if (mHomePresenter != null) {
            mHomePresenter.unregisterViewCallback(this);
        }
    }
}
