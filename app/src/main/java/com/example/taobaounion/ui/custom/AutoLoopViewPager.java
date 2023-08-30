package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;

public class AutoLoopViewPager extends ViewPager {
    private static final int DEFAULT_DELAY = 2000;
    private int duration = DEFAULT_DELAY;

    public AutoLoopViewPager(@NonNull Context context) {
        super(context);
    }

    public AutoLoopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoLoopStyle);
        duration = typedArray.getInteger(R.styleable.AutoLoopStyle_duration, DEFAULT_DELAY);
        typedArray.recycle();
    }


    private boolean mIsLoop;

    private Runnable mAction = new Runnable() {
        @Override
        public void run() {
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            if (mIsLoop) {
                postDelayed(this, duration);
            }
        }
    };

    public void setDuration(int duration){
        this.duration = duration;
    }

    public void startLoop() {
        mIsLoop = true;
        //反复执行轮播
        post(mAction);
    }

    public void stopLoop() {
        //Removes the specified Runnable from the message queue.
        removeCallbacks(mAction);
        mIsLoop = false;
    }
}
