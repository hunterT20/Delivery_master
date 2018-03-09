package com.thanhtuan.delivery.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.ui.detail.DetailActivity;
import com.thanhtuan.delivery.data.model.ItemChuaGiao;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListSaleAdapter extends RecyclerView.Adapter<ListSaleAdapter.SaleViewHolder> {
    private List<ItemChuaGiao> mItemChuaGiao;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    ListSaleAdapter(Context mContext) {
        this.mContext = mContext;
        this.mItemChuaGiao = new ArrayList<>();
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    void addList(List<ItemChuaGiao> chuaGiaoList){
        if (chuaGiaoList == null) return;
        this.mItemChuaGiao.addAll(chuaGiaoList);
        notifyDataSetChanged();
    }

    void clear(){
        this.mItemChuaGiao.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListSaleAdapter.SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_main, parent, false);
        return new SaleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSaleAdapter.SaleViewHolder holder, final int position) {
        final ItemChuaGiao itemChuaGiao = mItemChuaGiao.get(position);

        holder.txtvDonHang.setText(itemChuaGiao.getSaleReceiptId());
        holder.txtvNameCustomer.setText(itemChuaGiao.getCustomerName());
        holder.txtvPhone.setText(itemChuaGiao.getPhoneNumber());
        holder.txtvAddress.setText(itemChuaGiao.getAddress());
        holder.txtvSl.setText(String.valueOf(itemChuaGiao.getQuantity()));
        switch (itemChuaGiao.getStatus()){
            case 0:
                holder.txtvTrangThai.setText("Đang chờ giao hàng");
                break;
            case 10:
                holder.txtvTrangThai.setText("Đang giao hàng");
                break;
            case 11:
                holder.txtvTrangThai.setText("Hoàn tất giao hàng");
                break;
            case 3:
                holder.txtvTrangThai.setText("Hủy giao hàng");
                break;
            case 4:
                holder.txtvTrangThai.setText("Đã giao hàng");
                break;
        }

        if (itemChuaGiao.getStatus() != 0){
            SharePreferenceUtil.setValueSaleitem(mContext ,itemChuaGiao);
            SharePreferenceUtil.setValueStatus(mContext, itemChuaGiao.getStatus());

            Intent intent = new Intent(mContext, DetailActivity.class);
            mContext.startActivity(intent);
            ((MainActivity) mContext).finish();
        }

        holder.itemView.setOnClickListener(v -> {
            SharePreferenceUtil.setValueSaleitem(mContext ,itemChuaGiao);
            SharePreferenceUtil.setValueStatus(mContext, itemChuaGiao.getStatus());

            Intent intent = new Intent(mContext, DetailActivity.class);
            mContext.startActivity(intent);
            ((MainActivity) mContext).finish();
        });
    }

    @Override
    public int getItemCount() {
        if (mItemChuaGiao == null) return 0;
        return mItemChuaGiao.size();
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtvNameCustomer) TextView txtvNameCustomer;
        @BindView(R.id.txtvPhone) TextView txtvPhone;
        @BindView(R.id.txtvAddress) TextView txtvAddress;
        @BindView(R.id.txtvSL) TextView txtvSl;
        @BindView(R.id.txtvDonHang) TextView txtvDonHang;
        @BindView(R.id.txtvTrangThai) TextView txtvTrangThai;

        SaleViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
