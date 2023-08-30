package com.example.taobaounion.utils;

import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.example.taobaounion.presenter.impl.HomePresenterImpl;
import com.example.taobaounion.presenter.impl.OnSellPagePresenterImpl;
import com.example.taobaounion.presenter.impl.SearchPresenterIml;
import com.example.taobaounion.presenter.impl.SelectedPagePresenterImpl;
import com.example.taobaounion.presenter.impl.TicketPresenterImpl;

public class PresenterManager {
    private static final PresenterManager ourInstance = new PresenterManager();
    private final ICategoryPagerPresenter mCategoryPagePresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTicketPresenter;
    private final ISelectedPagePresenter mSelectedPagePresenter;
    private final IOnSellPagePresenter mOnSellPagePresenter;
    private final ISearchPresenter mSearchPresenter;

    public ITicketPresenter getTicketPresenter() {
        return mTicketPresenter;
    }

    public IHomePresenter getHomePresenter() {
        return mHomePresenter;
    }

    public ICategoryPagerPresenter getCategoryPagePresenter() {
        return mCategoryPagePresenter;
    }

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    public ISelectedPagePresenter getSelectedPagePresenter() {
        return mSelectedPagePresenter;
    }

    public IOnSellPagePresenter getOnSellPagePresenter() {
        return mOnSellPagePresenter;
    }

    public ISearchPresenter getSearchPresenter() {
        return mSearchPresenter;
    }

    private PresenterManager() {
        mCategoryPagePresenter = new CategoryPagerPresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        mSelectedPagePresenter = new SelectedPagePresenterImpl();
        mOnSellPagePresenter = new OnSellPagePresenterImpl();
        mSearchPresenter = new SearchPresenterIml();
    }
}
