package com.thanhtuan.delivery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Item;

import java.util.List;

/**
 * Created by Nusib on 5/18/2017.
 */

public class ListSaleAdapter extends RecyclerView.Adapter<ListSaleAdapter.SaleViewHolder> {
    private static final String TAG = "ListSaleAdapter";
    private List<Item> mItem;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ListSaleAdapter(List<Item> mItem, Context mContext) {
        this.mItem = mItem;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ListSaleAdapter.SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cardview, parent, false);
        return new SaleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListSaleAdapter.SaleViewHolder holder, int position) {
        //get song in mSong via position
        Item item = mItem.get(position);

        //bind data to viewholder
        holder.txtvNameCustomer.setText(item.getCustomerName());
        holder.txtvPhone.setText(item.getPhoneNumber());
        holder.txtvAddress.setText(item.getAddress());
        holder.txtvSl.setText(String.valueOf(item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvNameCustomer;
        private TextView txtvPhone;
        private TextView txtvAddress;
        private TextView txtvSl;

        public SaleViewHolder(View itemView) {
            super(itemView);
            txtvNameCustomer = (TextView) itemView.findViewById(R.id.txtvNameCustomer);
            txtvPhone = (TextView) itemView.findViewById(R.id.txtvPhone);
            txtvAddress = (TextView) itemView.findViewById(R.id.txtvAddress);
            txtvSl = (TextView) itemView.findViewById(R.id.txtvSL);
        }
    }
}
