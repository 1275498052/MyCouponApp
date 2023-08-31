package com.example.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.example.taobaounion.ui.adapter.LinearItemAdapter;
import com.example.taobaounion.ui.adapter.LooperPagerAdapter;
import com.example.taobaounion.ui.custom.AutoLoopViewPager;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.TicketUtil;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, LinearItemAdapter.OnListItemClickListener, LooperPagerAdapter.OnLooperPageItemClickListener {

    private CategoryPagerPresenterImpl mCategoryPagerPresenter;
    private int mId;

    @BindView(R.id.home_pager_content_list)
    public RecyclerView homePagerList;

    @BindView(R.id.looper_pager)
    public AutoLoopViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homePagerHeaderContainer;

    @BindView(R.id.home_pager_nested_scroller)
    public TbNestedScrollView homePagerNestedScroller;

    private LinearItemAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    @Override
    public void onResume() {
        looperPager.startLoop();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        looperPager.stopLoop();
    }

    @Override
    protected void onRetryClick() {
        loadData();
    }

    /**
     * 从HomePagerAdapter中将数据传入HomePagerFragment构造器
     * 初始化时将需要的信息存入HomePagerFragment中
     * @param category
     * @return
     */
    public static HomePagerFragment newInstance(Categories.DataDTO category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID, category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initView(View view) {
        //设置RecyclerView的item间距
        homePagerList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        homePagerList.addItemDecoration(new RecyclerView.ItemDecoration() {
            //设置偏移距离
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });
        //创建适配器
        mContentAdapter = new LinearItemAdapter();
        //得到适配器中的数据
        homePagerList.setAdapter(mContentAdapter);
        //创建轮播图适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        looperPager.setAdapter(mLooperPagerAdapter);
//        设置Refresh相关内容
        mTwinklingRefreshLayout.setEnableRefresh(false);//上拉刷新
        mTwinklingRefreshLayout.setEnableLoadmore(true);

    }

    @Override
    protected void initListener() {
        //设置数据回来
        mContentAdapter.setOnListItemClickListener(this);
        mLooperPagerAdapter.setOnLooperPageItemClickListener(this);
        //在LinerLayout布局有变化时测量高度
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //测量轮播图等头部的高度
                if (homePagerHeaderContainer == null) {
                    return;
                }
                int headerHeight = homePagerHeaderContainer.getMeasuredHeight();
//                LogUtils.d(this, "headerHeight -->" +headerHeight);
                homePagerNestedScroller.setHeaderHeight(headerHeight);

                int measuredHeight = homePagerParent.getMeasuredHeight();
//                LogUtils.d(this, "measuredHeight -->" + measuredHeight);
                ViewGroup.LayoutParams layoutParams = homePagerList.getLayoutParams();
                layoutParams.height = measuredHeight;
                homePagerList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //得到相对位置
//                int targetPosition = position % mLooperPagerAdapter.getSize();
                int targetPosition = position % 5;
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
//                LogUtils.d(HomePagerFragment.this, "触发了Loader more...");
                //去加载更多的内容
                if (mCategoryPagerPresenter != null) {
                    mCategoryPagerPresenter.loaderMore(mId);
                }
            }
        });

    }

    /**
     * 切换指示器,将指示器容器内添加的点列出，和当前位置对应就设置为显示选择状态
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            }else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void initPresenter() {
        mCategoryPagerPresenter = (CategoryPagerPresenterImpl) PresenterManager.getInstance().getCategoryPagePresenter();
        //当前类实现了View接口，可传递给presenter
        mCategoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void loadData() {
        //当前fragment携带的bundle
        Bundle arguments = this.getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        mId = arguments.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);
//        LogUtils.d(this, title + mId);
        //presenter为单例模式
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.getContentByCategoryId(mId);
        }
        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }

    }

    @Override
    public void onContentLoad(HomePagerContent content) {
        //更新UI
        mContentAdapter.setData(content.getData());
        setUpState(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return mId;
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtil.showToast("没有更多商品");
        if(mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtil.showToast("网络异常，请稍后重试");
        if(mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(HomePagerContent content) {
        //将数据给到适配器
        mContentAdapter.addData(content.getData());
        //结束加载控件刷新
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtil.showToast("加载了" + content.getData().size() + "条数据");
    }

    public void onLooperListLoaded(HomePagerContent content) {
//        LogUtils.d(this,"looper size - - > " + data.size());
        mLooperPagerAdapter.setData(content.getData());
        //中间点%数据的size不一定为0，所以显示的就不是第一个。
        //得到中间点的余数
        int dx = (Integer.MAX_VALUE / 2) % 5;
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;
        //设置到中间点
        looperPager.setCurrentItem(targetCenterPosition);
        //LogUtils.d(this," url  -- >" + data.get(0).getPict_url());
        //把点都清除
        looperPointContainer.removeAllViews();

        //添加指示器
        for(int i = 0; i < 5; i++) {
            View point = new View(getContext());
            int size = SizeUtils.dip2px(getContext(),8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size,size);
            //距离两旁控件的间距
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(),5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(),5);
            point.setLayoutParams(layoutParams);
            if(i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    protected void release() {
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo item) {
//        LogUtils.d(this, "name --> " + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
//        String title = item.getTitle();
//        String url = item.getCoupon_click_url();
//        if (TextUtils.isEmpty(url)) {
//            url = item.getClick_url();
//        }
//        String cover = item.getPict_url();
//        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
//        ticketPresenter.getTicket(title, url, cover);
//        startActivity(new Intent(getContext(), TicketActivity.class));
    }

    @Override
    public void onLooperItemClick(IBaseInfo item) {
        handleItemClick(item);
//        LogUtils.d(this, "name --> " + item.getTitle());
    }
}
