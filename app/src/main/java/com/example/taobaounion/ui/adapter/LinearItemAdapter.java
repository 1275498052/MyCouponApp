package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.example.taobaounion.utils.UrlUtils;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinearItemAdapter extends RecyclerView.Adapter<LinearItemAdapter.InnerHolder> {
    List<ILinearItemInfo> data = new LinkedList<>();
    private OnListItemClickListener mItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content, parent, false);
//        LogUtils.d(this, "onCreateViewHolder");
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        ILinearItemInfo dataDTO = data.get(position);
        //设置数据
//        LogUtils.d(this, "onBindViewHolder");
        holder.setData(dataDTO);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(dataDTO);
                }
            }
        });
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnListItemClickListener {
        void onItemClick(IBaseInfo item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //此处的setData为外部传入
    public void setData(List<? extends ILinearItemInfo> item) {
        data.clear();
        data.addAll(item);
        notifyDataSetChanged();
    }

    public void addData(List<? extends ILinearItemInfo> item) {
        //添加前拿到原来的数据大小
        int preSize = data.size();
        data.addAll(item);
        //设置局部数据更新
        notifyItemRangeChanged(preSize, item.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_price)
        public TextView offPrice;

        @BindView(R.id.goods_after_off_price)
        public TextView finalprice;

        @BindView(R.id.goods_original_price)
        public TextView originalPriceTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCount;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * 将item里面的控件设置数据
         */
        public void setData(ILinearItemInfo item) {
            Context context = itemView.getContext();
            title.setText(item.getTitle());
            //计算itemView的大小
//            int width = cover.getLayoutParams().width;
//            int height = cover.getLayoutParams().height;
//            int coverSize = width > height ? width : height;
            Glide.with(itemView.getContext()).load(UrlUtils.getCoverPath(item.getCover())).into(cover);
            long couponAmount = item.getCouponAmount();
            String finalPrice = item.getFinalPrise();
            // LogUtils.d(this,"final prise -- > " + finalPrise);
            float resultPrise = Float.parseFloat(finalPrice) - couponAmount;
            //LogUtils.d(this,"result prise -- -> " + resultPrise);

            finalprice.setText(String.format("%.2f",resultPrise));
            offPrice.setText(String.format(context.getString(R.string.text_goods_off_prise),couponAmount));
            //setPaintFlags设置中划线
            originalPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            originalPriceTv.setText(String.format(context.getString(R.string.text_goods_original_prise),finalPrice));
            sellCount.setText(String.format(context.getString(R.string.text_goods_sell_count),item.getVolume()));
        }
    }
}
