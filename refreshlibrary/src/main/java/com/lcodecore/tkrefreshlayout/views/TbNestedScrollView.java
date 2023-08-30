package com.lcodecore.tkrefreshlayout.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.taobaounion.utils.LogUtils;

public class TbNestedScrollView extends NestedScrollView {
    private static final String TAG = "TbNestedScrollView";
    private int headerHeight = 0;
    private int mCurHeight = 0;
    private RecyclerView mRecycleView;

    public TbNestedScrollView(Context context) {
        super(context);
    }

    public TbNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TbNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
//        Log.d(TAG, "headerHeight -->" + this.headerHeight);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
//        Log.d(TAG, "mCurHeight -- >" + mCurHeight);
        if (target != null) {
            this.mRecycleView = (RecyclerView)target;
        }
        if (mCurHeight < headerHeight) {
            scrollBy(dx, dy);
            consumed[0] = dx;
            consumed[1] = dy;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    /**
     * @param l    当前水平位置
     * @param t    当前垂直位置
     * @param oldl 未移动前的水平位置
     * @param oldt 未移动前的垂直位置
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        Log.d(TAG, "vertical -->" + t);
        mCurHeight = t;
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public boolean isBottom() {
        if (mRecycleView != null) {
            return !mRecycleView.canScrollVertically(1);
        }
        return false;
    }
}
