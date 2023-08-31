package com.example.taobaounion.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.OnSellFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.example.taobaounion.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity{
    /**
     * butterknife通过反射赋值，不能用private
     */
    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mNavigationView;

    public HomeFragment mHomeFragment;
    private OnSellFragment mOnSellFragment;
    private SearchFragment mSearchFragment;
    private SelectedFragment mSelectedFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initView() {
        initFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initFragment() {
        mHomeFragment = new HomeFragment();
        mOnSellFragment = new OnSellFragment();
        mSearchFragment = new SearchFragment();
        mSelectedFragment = new SelectedFragment();
        //在Fragment的实际操作中其实并不是使用Fragment来进行操作的,而是通过使用FragmentTransaction进行操作的
        mFragmentManager = getSupportFragmentManager();
        //默认精选页面
        switchFragment(mHomeFragment);
    }

    private void initListener() {
        mNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        LogUtils.d(this, "切换到首页");
                        switchFragment(mHomeFragment);
                        break;
                    case R.id.selected:
                        LogUtils.d(this, "切换到精选");
                        switchFragment(mSelectedFragment);
                        break;
                    case R.id.red_packet:
                        LogUtils.d(this, "切换到特惠");
                        switchFragment(mOnSellFragment);
                        break;
                    case R.id.search:
                        LogUtils.d(this, "切换到搜索");
                        switchFragment(mSearchFragment);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 上一次显示的Fragment
     */
    private BaseFragment lastOneFragment = null;

    //将已加载的Fragment储存，防止每次切换都进行销毁
    private void switchFragment(BaseFragment targetFragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (lastOneFragment != null) {
            fragmentTransaction.hide(lastOneFragment);
        }
        if (targetFragment.isAdded()) {
            fragmentTransaction.show(targetFragment);
        }else {
            fragmentTransaction.add(R.id.main_page_container,targetFragment);
        }
        lastOneFragment = targetFragment;
//        fragmentTransaction.replace(R.id.main_page_container, targetFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void switch2Search() {
//        switchFragment(mSearchFragment);
        mNavigationView.setSelectedItemId(R.id.search);
    }
}