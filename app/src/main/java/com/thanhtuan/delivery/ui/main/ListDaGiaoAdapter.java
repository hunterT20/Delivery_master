package com.thanhtuan.delivery.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.ItemDaGiao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListDaGiaoAdapter extends RecyclerView.Adapter<ListDaGiaoAdapter.DaGiaoViewHolder> {
    private List<ItemDaGiao> mItemDaGiao;
    private LayoutInflater mLayoutInflater;

    ListDaGiaoAdapter(@NonNull Context mContext) {
        this.mItemDaGiao = new ArrayList<>();
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    void addList(List<ItemDaGiao> daGiaoList){
        if (daGiaoList == null) return;
        this.mItemDaGiao.addAll(daGiaoList);
        notifyDataSetChanged();
    }

    void clear(){
        this.mItemDaGiao.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListDaGiaoAdapter.DaGiaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_dagiao, parent, false);
        return new DaGiaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDaGiaoAdapter.DaGiaoViewHolder holder, int position) {
        final ItemDaGiao itemDaGiao = mItemDaGiao.get(position);
        holder.txtvDonHang.setText(itemDaGiao.getSaleReceiptId());
        holder.txtvAddress.setText(itemDaGiao.getAddress());
        holder.txtvTrangThai.setText(itemDaGiao.getStatus());
    }

    @Override
    public int getItemCount() {
        if (mItemDaGiao == null) return 0;
        return mItemDaGiao.size();
    }

    class DaGiaoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtvAddress) TextView txtvAddress;
        @BindView(R.id.txtvDonHang) TextView txtvDonHang;
        @BindView(R.id.txtvTrangThai) TextView txtvTrangThai;

        DaGiaoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
