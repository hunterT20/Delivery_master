package com.thanhtuan.delivery.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Product;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ProductViewHolder> {
    private static final String TAG = "ListProductAdapter";
    private List<Product> mProduct;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ListProductAdapter(List<Product> mProduct, Context mContext) {
        this.mProduct = mProduct;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ListProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cardview_detail, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListProductAdapter.ProductViewHolder holder, int position) {
        //get song in mSong via position
        final Product product = mProduct.get(position);

        //bind data to viewholder
        holder.txtvSKU.setText(position + 1 + ". " + product.getSKU());
        holder.txtvDonGia.setText(product.getPrice().toString() + " VNĐ");
        holder.txtvSoLuong.setText(String.valueOf(product.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mProduct.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvSKU;
        private TextView txtvDonGia;
        private TextView txtvSoLuong;
        ProductViewHolder(View itemView) {
            super(itemView);
            txtvSKU = (TextView) itemView.findViewById(R.id.txtvSKU);
            txtvDonGia = (TextView) itemView.findViewById(R.id.txtvDonGia);
            txtvSoLuong = (TextView) itemView.findViewById(R.id.txtvSoLuong);
        }
    }
}
