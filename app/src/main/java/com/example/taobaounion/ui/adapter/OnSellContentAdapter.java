package com.example.taobaounion.ui.adapter;

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
import com.example.taobaounion.model.bean.IBaseInfo;
import com.example.taobaounion.model.bean.OnSellContent;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnSellContentAdapter extends RecyclerView.Adapter<OnSellContentAdapter.InnerHolder> {
    List<OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO> mData = new ArrayList<>();
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sell_content, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO mapDataBean = mData.get(position);
        holder.setData(mapDataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContentItemClickListener != null) {
                    mContentItemClickListener.onSellItemClick(mapDataBean);
                }
            }
        });
    }

    private OnSellPageItemClickListener mContentItemClickListener = null;

    public void setOnSellPageItemClickListener(OnSellPageItemClickListener listener) {
        this.mContentItemClickListener = listener;
    }

    public interface OnSellPageItemClickListener {
        void onSellItemClick(IBaseInfo data);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(OnSellContent result) {
        mData.clear();
        mData.addAll(result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyDataSetChanged();
    }

    public void addData(OnSellContent moreResult) {
        int size = mData.size();
        mData.addAll(moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyItemRangeChanged(size - 1, mData.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.on_sell_cover)
        public ImageView cover;

        @BindView(R.id.on_sell_content_title_tv)
        public TextView titleTv;

        @BindView(R.id.on_sell_origin_prise_tv)
        public TextView originalPriseTv;

        @BindView(R.id.on_sell_off_prise_tv)
        public TextView offPriseTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO item) {
            titleTv.setText(item.getTitle());
            String pict_url = item.getPict_url();
            Glide.with(itemView).load(UrlUtils.getCoverPath(pict_url)).into(cover);
            String originalPrise = item.getZk_final_price();
            originalPriseTv.setText("￥" + originalPrise + " ");
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            int couponAmount = item.getCoupon_amount();
            float originPriseFloat = Float.parseFloat(originalPrise);
            float finalPrise = originPriseFloat - couponAmount;
            offPriseTv.setText("券后价：" + String.format("%.2f",finalPrise));
        }
    }
}
