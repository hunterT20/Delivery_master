package com.thanhtuan.delivery.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ProductViewHolder> {
    private List<Product> mProduct;
    private LayoutInflater mLayoutInflater;

    ListProductAdapter(Context mContext) {
        this.mProduct = new ArrayList<>();
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    void addList(List<Product> productList){
        if (productList == null) return;
        this.mProduct.addAll(productList);
        notifyDataSetChanged();
    }

    void reset(){
        this.mProduct.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_detail, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProductAdapter.ProductViewHolder holder, int position) {
        final Product product = mProduct.get(position);

        holder.txtvSKU.setText(position + 1 + ". " + product.getItemName());
        holder.txtvSoLuong.setText(String.valueOf(product.getQuantity()));
    }

    @Override
    public int getItemCount() {
        if (mProduct == null) return 0;
        return mProduct.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtvSKU) TextView txtvSKU;
        @BindView(R.id.txtvSoLuong) TextView txtvSoLuong;
        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
