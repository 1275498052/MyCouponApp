package com.example.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.model.bean.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.custom.LoadingView;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.ToastUtil;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private static final int dd= 1;
    private static int a= 1;

    private ITicketPresenter mTicketPresenter;

    private boolean mHasTabaoApp = false;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_back_press)
    public View backPress;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;


    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mOpenOrCopyBtn;


    @BindView(R.id.ticket_cover_loading)
    public View loadingView;


    @BindView(R.id.ticket_load_retry)
    public View retryLoadText;

    @Override
    protected void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        mTicketPresenter.registerViewCallback(this);

        //包名是：com.taobao.taobao
        // 检查是否有安装淘宝应用
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao",PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTabaoApp = packageInfo != null;
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTabaoApp = false;
        }
        LogUtils.d(this,"mHasTabaoApp -- > " + mHasTabaoApp);
        //根据这个值去修改UI
        mOpenOrCopyBtn.setText(mHasTabaoApp ? "打开淘宝领券" : "复制淘口令");
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
//  com.taobao.taobao      start com.taobao.tao.TBMainActivity
        mOpenOrCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                //拿到内容
                String ticketCode = mTicketCode.getText().toString().trim();
                LogUtils.d(TicketActivity.this,"ticketCode --- > " + ticketCode);
                //ClipboardManager： 表示一个剪贴板
                //ClipData： 剪贴板中保存的所有剪贴数据集（剪贴板可同时复制/保存多条多种数据条目）
                //得到粘贴板管理对象
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code",ticketCode);
                cm.setPrimaryClip(clipData);
                if (mHasTabaoApp) {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                }else {
                    //没有提示复制成功
                    ToastUtil.showToast("已经复制,粘贴分享,或打开淘宝");
                }
            }
        });

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mCover != null && !TextUtils.isEmpty(cover)) {
            Glide.with(this).load(UrlUtils.getCoverPath(cover)).into(mCover);
        }
        //设置一下code
        if(result != null && result.getData().getTbk_tpwd_create_response() != null) {
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
    }

    @Override
    public void onError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    protected void release() {
        mTicketPresenter.unregisterViewCallback(this);
    }
}
