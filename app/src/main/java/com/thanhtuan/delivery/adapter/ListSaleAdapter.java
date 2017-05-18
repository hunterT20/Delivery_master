package com.thanhtuan.delivery.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.activity.DetailActivity;
import com.thanhtuan.delivery.model.Item;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


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
    public void onBindViewHolder(ListSaleAdapter.SaleViewHolder holder, final int position) {
        //get song in mSong via position
        final Item item = mItem.get(position);

        //bind data to viewholder
        holder.txtvNameCustomer.setText(item.getCustomerName());
        holder.txtvPhone.setText(item.getPhoneNumber());
        holder.txtvAddress.setText(item.getAddress());
        holder.txtvSl.setText(String.valueOf(item.getQuantity()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefs = mContext.getSharedPreferences("MyPre",MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(item);
                Log.e("adapter", json);
                prefsEditor.putString("SaleItem", json);
                prefsEditor.apply();

                Intent intent = new Intent(mContext, DetailActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvNameCustomer;
        private TextView txtvPhone;
        private TextView txtvAddress;
        private TextView txtvSl;

        SaleViewHolder(final View itemView) {
            super(itemView);
            txtvNameCustomer = (TextView) itemView.findViewById(R.id.txtvNameCustomer);
            txtvPhone = (TextView) itemView.findViewById(R.id.txtvPhone);
            txtvAddress = (TextView) itemView.findViewById(R.id.txtvAddress);
            txtvSl = (TextView) itemView.findViewById(R.id.txtvSL);
        }
    }
}
