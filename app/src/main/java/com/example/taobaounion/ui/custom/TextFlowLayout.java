package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taobaounion.R;
import com.example.taobaounion.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFlowLayout extends ViewGroup {
    public static final float DEFAULT_SPACE = 10;
    private float mItemHorizontalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;
    private List<String> mTextList = new ArrayList<>();
    private int mWidth;
    private int mItemHeight;


    public TextFlowLayout(Context context) {
        this(context, null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        //拿到相关属性，如果没有取值就需要设置默认值
        mItemHorizontalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        ta.recycle();
    }

    public int getContentSize() {
        return mTextList.size();
    }

    public void setTextList(List<String> textList) {
        removeAllViews();
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        Collections.reverse(mTextList);
        //遍历内容
        for (String text : mTextList) {
            //添加子view
            //LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view,this,true);
            // 等价于
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null) {
                        mItemClickListener.onFlowItemClick(text);
                    }
                }
            });
            addView(item);
        }
//        LogUtils.d(TextFlowLayout.class, "countSize -- > " + getChildCount());
    }

    private List<List<View>> mLines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d("TextFlowLayout", "size -- > " + getChildCount());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() == 0) return;
        mLines.clear();
        mWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        //测子View的宽高
        List<View> line = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            if (canBeAdded(line, view)) {
                line.add(view);
            }else {
                mLines.add(new ArrayList<>(line));
                line.clear();
                line.add(view);
            }
        }
        mLines.add(line);
        mItemHeight = getChildAt(0).getMeasuredHeight();
        int mHeight = (int)((mLines.size() + 1) * mItemVerticalSpace) + mItemHeight * mLines.size();
        setMeasuredDimension(mWidth, mHeight);

    }

    private boolean canBeAdded(List<View> line, View curView) {
        int curWidth = 0;
        for (View view : line) {
            curWidth += view.getMeasuredWidth() + mItemHorizontalSpace;
        }
        curWidth += curView.getMeasuredWidth() + mItemHorizontalSpace;
        return curWidth <= mWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int topOffset = (int) mItemHorizontalSpace;
        for(List<View> views : mLines) {
            //views是每一行
            int leftOffset = (int) mItemHorizontalSpace;
            for (View view : views) {
                //每一行里的每个item
                view.layout(leftOffset, topOffset, leftOffset + view.getMeasuredWidth(), topOffset + view.getMeasuredHeight());
                //
                leftOffset += view.getMeasuredWidth() + mItemHorizontalSpace;
            }
            topOffset += mItemHeight + mItemHorizontalSpace;
        }
    }

    private OnFlowTextItemClickListener mItemClickListener;
    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener {
        void onFlowItemClick(String text);
    }
}
