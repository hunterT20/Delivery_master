package com.thanhtuan.delivery.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Item_DaGiao;

import java.util.List;

/**
 * Created by Nusib on 6/10/2017.
 */

public class ListDaGiaoAdapter extends RecyclerView.Adapter<ListDaGiaoAdapter.DaGiaoViewHolder> {
    private static final String TAG = "ListSaleAdapter";
    private List<Item_DaGiao> mItemDaGiao;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ListDaGiaoAdapter(List<Item_DaGiao> mItemDaGiao, Context mContext) {
        this.mItemDaGiao = mItemDaGiao;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public ListDaGiaoAdapter.DaGiaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cardview_dagiao, parent, false);
        return new DaGiaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListDaGiaoAdapter.DaGiaoViewHolder holder, int position) {
        final Item_DaGiao itemDaGiao = mItemDaGiao.get(position);

        //bind data to viewholder
        holder.txtvDonHang.setText(itemDaGiao.getSaleReceiptId());
        holder.txtvAddress.setText(itemDaGiao.getAddress());
        holder.txtvTrangThai.setText(itemDaGiao.getStatus());
    }

    @Override
    public int getItemCount() {
        return mItemDaGiao.size();
    }

    class DaGiaoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvAddress;
        private TextView txtvDonHang;
        private TextView txtvTrangThai;

        DaGiaoViewHolder(View itemView) {
            super(itemView);
            txtvAddress = (TextView) itemView.findViewById(R.id.txtvAddress);
            txtvDonHang = (TextView) itemView.findViewById(R.id.txtvDonHang);
            txtvTrangThai = (TextView) itemView.findViewById(R.id.txtvTrangThai);
        }
    }
}
