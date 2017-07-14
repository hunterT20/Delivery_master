package com.thanhtuan.delivery.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.view.activity.DetailActivity;
import com.thanhtuan.delivery.view.activity.MainActivity;
import com.thanhtuan.delivery.model.Item_ChuaGiao;
import com.thanhtuan.delivery.util.SharePreferenceUtil;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ListSaleAdapter extends RecyclerView.Adapter<ListSaleAdapter.SaleViewHolder> {
    private List<Item_ChuaGiao> mItemChuaGiao;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ListSaleAdapter(List<Item_ChuaGiao> mItemChuaGiao, Context mContext) {
        this.mItemChuaGiao = mItemChuaGiao;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ListSaleAdapter.SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_main, parent, false);
        return new SaleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListSaleAdapter.SaleViewHolder holder, final int position) {
        //get song in mSong via position
        final Item_ChuaGiao itemChuaGiao = mItemChuaGiao.get(position);

        //bind data to viewholder
        holder.txtvDonHang.setText(itemChuaGiao.getSaleReceiptId());
        holder.txtvNameCustomer.setText(itemChuaGiao.getCustomerName());
        holder.txtvPhone.setText(itemChuaGiao.getPhoneNumber());
        holder.txtvAddress.setText(itemChuaGiao.getAddress());
        holder.txtvSl.setText(String.valueOf(itemChuaGiao.getQuantity()));
        switch (itemChuaGiao.getStatus()){
            case 0:
                holder.txtvTrangThai.setText("Đang chờ giao hàng");
                break;
            case 1:
                holder.txtvTrangThai.setText("Đang giao hàng");
                break;
            case 2:
                holder.txtvTrangThai.setText("Hoàn tất giao hàng");
                break;
            case 3:
                holder.txtvTrangThai.setText("Hủy giao hàng");
                break;
            case 4:
                holder.txtvTrangThai.setText("Đã giao hàng");
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceUtil.setValueSaleitem(mContext ,itemChuaGiao);
                SharePreferenceUtil.setValueStatus(mContext, itemChuaGiao.getStatus());

                Intent intent = new Intent(mContext, DetailActivity.class);
                mContext.startActivity(intent);
                ((MainActivity) mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemChuaGiao.size();
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvNameCustomer, txtvPhone, txtvAddress, txtvSl, txtvDonHang, txtvTrangThai;

        SaleViewHolder(final View itemView) {
            super(itemView);
            txtvNameCustomer = (TextView) itemView.findViewById(R.id.txtvNameCustomer);
            txtvPhone = (TextView) itemView.findViewById(R.id.txtvPhone);
            txtvAddress = (TextView) itemView.findViewById(R.id.txtvAddress);
            txtvSl = (TextView) itemView.findViewById(R.id.txtvSL);
            txtvDonHang = (TextView) itemView.findViewById(R.id.txtvDonHang);
            txtvTrangThai = (TextView) itemView.findViewById(R.id.txtvTrangThai);
        }
    }
}
