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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.SelectedPageContentAdapter;
import com.example.taobaounion.ui.adapter.SelectedPageLeftAdapter;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.view.ISelectedPageCallback;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback, SelectedPageLeftAdapter.OnLeftItemClickListener, SelectedPageContentAdapter.onContentItemClickListener {

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;

    @BindView(R.id.right_content_list)
    public RecyclerView rightContentList;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;


    private ISelectedPagePresenter mSelectedPagePresenter;
    private SelectedPageLeftAdapter mLeftAdapter;
    private SelectedPageContentAdapter mRightAdapter;

    //加载带标题的视图
    @Override
    protected View loadRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_selected;
    }

    @Override
    protected void initView(View view) {
        setUpState(State.SUCCESS);

        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(mLeftAdapter);

        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRightAdapter = new SelectedPageContentAdapter();
        rightContentList.setAdapter(mRightAdapter);

        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(),4);
                int leftAndRight = SizeUtils.dip2px(getContext(),6);
                outRect.left = leftAndRight;
                outRect.right = leftAndRight;
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
            }
        });

        barTitleTv.setText("精选宝贝");
    }

    @Override
    protected void initListener() {
        mLeftAdapter.setOnLeftItemClickListener(this);
        mRightAdapter.setContentItemClickListener(this);
    }

    @Override
    public void onLeftItemClick(Categories.DataDTO item) {
        mSelectedPagePresenter.getContentByCategory(item);
    }

    @Override
    public void onContentItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
//        String url = item.getCoupon_click_url();
//        String cover = item.getPict_url();
//        String title = item.getTitle();
//        if (TextUtils.isEmpty(url)) {
//            url = item.getClick_url();
//        }
//        PresenterManager.getInstance().getTicketPresenter().getTicket(title, url, cover);
//        startActivity(new Intent(getContext(), TicketActivity.class));
    }

    @Override
    protected void initPresenter() {
        mSelectedPagePresenter = PresenterManager.getInstance().getSelectedPagePresenter();
        mSelectedPagePresenter.registerViewCallback(this);
        mSelectedPagePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {
        setUpState(State.SUCCESS);
        mLeftAdapter.setData(categories);
    }

    @Override
    public void onContentLoaded(HomePagerContent content) {
        mRightAdapter.setData(content.getData());
    }

    @Override
    protected void onRetryClick() {
        mSelectedPagePresenter.reloadContent();
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
        mSelectedPagePresenter.unregisterViewCallback(this);
    }
}
