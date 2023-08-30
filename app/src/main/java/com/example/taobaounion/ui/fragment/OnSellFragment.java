package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.model.bean.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.OnSellContentAdapter;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.view.IOnSellPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallback, OnSellContentAdapter.OnSellPageItemClickListener {
    public static final int DEFAULT_SPAN_COUNT = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentRv;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    private OnSellContentAdapter mOnSellContentAdapter;

    private IOnSellPagePresenter mOnSellPagePresenter;

    @Override
    protected View loadRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false);
    }
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }
    @Override
    protected void initView(View view) {
        setUpState(State.SUCCESS);
        mContentRv.setLayoutManager(new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT));
        mOnSellContentAdapter = new OnSellContentAdapter();
        mContentRv.setAdapter(mOnSellContentAdapter);
        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),2.5f);
                outRect.left = SizeUtils.dip2px(getContext(),2.5f);
                outRect.right = SizeUtils.dip2px(getContext(),2.5f);
            }
        });
        mTwinklingRefreshLayout.setEnableLoadmore(true);
        mTwinklingRefreshLayout.setEnableRefresh(false);
    }

    @Override
    protected void initListener() {
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多的内容
                if(mOnSellPagePresenter != null) {
                    mOnSellPagePresenter.loaderMore();
                }
            }
        });

        mOnSellContentAdapter.setOnSellPageItemClickListener(this);
    }

    @Override
    public void onSellItemClick(IBaseInfo data) {
        TicketUtil.toTicketPage(getContext(), data);
//        //特惠列表内容被点击
//        //处理数据
//        String title = data.getTitle();
//        //详情的地址
//        String url = data.getCoupon_click_url();
//        if(TextUtils.isEmpty(url)) {
//            url = data.getClick_url();
//        }
//        String cover = data.getPict_url();
//        //拿到tiketPresenter去加载数据
//        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
//        ticketPresenter.getTicket(title,url,cover);
//        startActivity(new Intent(getContext(), TicketActivity.class));
    }

    @Override
    protected void initPresenter() {
        mOnSellPagePresenter = PresenterManager.getInstance().getOnSellPagePresenter();
        mOnSellPagePresenter.registerViewCallback(this);
        mOnSellPagePresenter.getOnSellContent();
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        setUpState(State.SUCCESS);
        mOnSellContentAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        mTwinklingRefreshLayout.finishLoadmore();
        mOnSellContentAdapter.addData(moreResult);
    }

    @Override
    public void onMoreLoadedError() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("网络异常,请稍后重试.");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("没有更多的内容...");
    }

    @Override
    protected void onRetryClick() {
        mOnSellPagePresenter.reLoad();
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
        if (mOnSellPagePresenter != null) {
            mOnSellPagePresenter.unregisterViewCallback(this);
        }
    }

}
