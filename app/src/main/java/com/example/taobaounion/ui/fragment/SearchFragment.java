package com.example.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.bean.Histories;
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.model.bean.ILinearItemInfo;
import com.example.taobaounion.model.bean.SearchRecommend;
import com.example.taobaounion.model.bean.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.ui.adapter.LinearItemAdapter;
import com.example.taobaounion.ui.custom.TextFlowLayout;
import com.example.taobaounion.utils.KeyboardUtil;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.view.ISearchPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchPageCallback, LinearItemAdapter.OnListItemClickListener {

    private ISearchPresenter mSearchPresenter;

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoriesView;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_container)
    public View mHistoriesContainer;


    @BindView(R.id.search_history_delete)
    public View mHistoryDelete;


    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mCleanInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;


    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;
    private LinearItemAdapter mLinearItemAdapter;

    @Override
    protected View loadRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }


    @Override
    protected int getResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(View view) {
        setUpState(State.SUCCESS);
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLinearItemAdapter = new LinearItemAdapter();
        //设置刷新控件
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
        mSearchList.setAdapter(mLinearItemAdapter);
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 1.5f);
            }
        });
    }

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        //获取搜索推荐词
        mSearchPresenter.getRecommendWords();
//        String keyWords = new String("皮鞋");

//        mSearchPresenter.doSearch("pixie");
        mSearchPresenter.getHistories();
    }

    @Override
    protected void initListener() {
        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchPresenter.delHistories();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mSearchInputBox.getText().toString().trim();
                if (s != null && s.length() > 0 ) {
                    toSearch(s);
                }else {
                    ToastUtil.showToast("请输入内容");
                }
                KeyboardUtil.hide(getContext(), v);
            }
        });
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String trim = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(trim)) {
                        LogUtils.d(SearchFragment.this, trim);
                        toSearch(trim);
                    }else {
                        ToastUtil.showToast("请输入内容");
                    }
                }
                return false;
            }
        });
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化时候的通知
                //LogUtils.d(SearchFragment.this,"input text === > " + s.toString().trim());
                //如果长度不为0，那么显示删除按钮
                //否则隐藏删除按钮
                int length = s.toString().trim().length();
                mCleanInputBtn.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(length > 0 ? "搜索" : "取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                switchTOHistoryPage();
            }
        });
        mLinearItemAdapter.setOnListItemClickListener(this);
        mHistoriesView.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                toSearch(text);
            }
        });
        mRecommendView.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                toSearch(text);
            }
        });
        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多内容
                if(mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });
    }

    private void switchTOHistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
        if (mRecommendView.getContentSize() != 0) {
            mRecommendContainer.setVisibility(View.VISIBLE);
        }else {
            mRecommendContainer.setVisibility(View.GONE);
        }
        mSearchList.setVisibility(View.GONE);
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
//        List<String> histories1 = histories.getHistories();
//        boolean b = histories == null;
//        LogUtils.d(this, "onHistoriesLoaded -- > " + b);
//        mHistoriesView.setTextList(histories1);

        setUpState(State.SUCCESS);
//        LogUtils.d(this, "histories -- > " + histories);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoriesContainer.setVisibility(View.GONE);
        } else {
            mHistoriesContainer.setVisibility(View.VISIBLE);
            mHistoriesView.setTextList(histories.getHistories());
        }
    }

    @Override
    public void onHistoriesDeleted() {
        mSearchPresenter.getHistories();
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
        mHistoriesContainer.setVisibility(View.GONE);
        mRecommendContainer.setVisibility(View.GONE);
        mSearchList.setVisibility(View.VISIBLE);
        try {
            mLinearItemAdapter.setData(result.getData()
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());
        } catch(Exception e) {
            e.printStackTrace();
            //切换到搜搜内容为空
            setUpState(State.EMPTY);
        }

    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        List<? extends ILinearItemInfo> data = result.getData()
                .getTbk_dg_material_optional_response()
                .getResult_list()
                .getMap_data();
        mLinearItemAdapter.addData(data);
        mRefreshContainer.finishLoadmore();
        //提示用户加载到的内容
        ToastUtil.showToast("加载到了" + data.size() + "条记录");
    }

    @Override
    public void onMoreLoadedError() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("网络异常，请稍后重试");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("没有更多数据");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataDTO> recommendWords) {
        setUpState(State.SUCCESS);
//        LogUtils.d(this, "recommendWords size --- > " + recommendWords.size());
        List<String> recommendKeywords = new ArrayList<>();

        for (SearchRecommend.DataDTO item : recommendWords) {
            recommendKeywords.add(item.getKeyword());
        }
        if (recommendWords == null || recommendWords.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendView.setTextList(recommendKeywords);
            mRecommendContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void retry() {
        mSearchPresenter.research();
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
        mSearchPresenter.unregisterViewCallback(this);
    }

    @Override
    public void onItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
    }

    private void toSearch(String text) {
        if(mSearchPresenter != null) {
            mSearchList.scrollToPosition(0);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            //mSearchInputBox.setSelection(text.length());
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPresenter.doSearch(text);
        }
    }
}
