package com.example.taobaounion.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.model.bean.Categories;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageLeftAdapter extends RecyclerView.Adapter<SelectedPageLeftAdapter.InnerHolder> {
    private List<Categories.DataDTO> mData = new ArrayList<>();
    private int mCurrentSelectedPosition = 0;


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_left, parent, false);
        return new InnerHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setData(mData.get(position).getTitle());
        TextView itemTv = holder.itemTv;
        if(mCurrentSelectedPosition != position) {
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.colorTabSelected,null));
        } else {
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.colorEEEEEE,null));
        }
        itemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null && mCurrentSelectedPosition != position) {
                    mCurrentSelectedPosition = position;
                    mItemClickListener.onLeftItemClick(mData.get(mCurrentSelectedPosition));
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(Categories categories) {
        mData.clear();
        List<Categories.DataDTO> data = categories.getData();
        mData.addAll(data.subList(data.size() - 5, data.size()));
        notifyDataSetChanged();

        //在第一次将数据设置进来的时候出发右边页面加载
        if(mData.size() > 0) {
            mItemClickListener.onLeftItemClick(mData.get(mCurrentSelectedPosition));
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.left_category_tv)
        public TextView itemTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String title) {
            itemTv.setText(title);
        }
    }

    private OnLeftItemClickListener mItemClickListener;

    public void setOnLeftItemClickListener(OnLeftItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnLeftItemClickListener {
        void onLeftItemClick(Categories.DataDTO item);
    }
}
