package com.example.taobaounion.ui.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.model.bean.ILinearItemInfo;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {
    List<ILinearItemInfo> mData = new ArrayList<>();
    private String mCoverUrl;
    private String mTitle;
    private String mClickUrl;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, @SuppressLint("RecyclerView") int position) {
        ILinearItemInfo data = mData.get(position);
        holder.setData(data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnContentItemClickListener != null) {
                    mOnContentItemClickListener.onContentItemClick(data);
                }
            }
        });
    }

    private onContentItemClickListener mOnContentItemClickListener;

    public void setContentItemClickListener(onContentItemClickListener onContentItemClickListener) {
        this.mOnContentItemClickListener = onContentItemClickListener;
    }

    public interface onContentItemClickListener {
        void onContentItemClick(IBaseInfo item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<? extends ILinearItemInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.selected_cover)
        public ImageView cover;

        @BindView(R.id.selected_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.selected_title)
        public TextView title;

        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;

        @BindView(R.id.selected_original_prise)
        public TextView originalPriseTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(ILinearItemInfo item) {
            String pictUrl = item.getCover();
            mCoverUrl = UrlUtils.getCoverPath(pictUrl);
//            LogUtils.d(SelectedPageContentAdapter.class, "cover -- > " + url);
            Glide.with(itemView).load(mCoverUrl).into(cover);
            mTitle = item.getTitle();
            title.setText(mTitle);
            //如果没有优惠券就隐藏领券按钮
            mClickUrl = item.getUrl();
//            LogUtils.d(SelectedPageContentAdapter.class, "优惠链接 --> " + mClickUrl);
            if (TextUtils.isEmpty(mClickUrl)) {
                originalPriseTv.setText("晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);
                offPriseTv.setVisibility(View.GONE);
            } else {
                offPriseTv.setVisibility(View.VISIBLE);
                offPriseTv.setText(String.format(itemView.getContext().getString(R.string.text_goods_off_price), item.getCouponAmount()));
                originalPriseTv.setText("原价：" + item.getFinalPrise());
                buyBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}
