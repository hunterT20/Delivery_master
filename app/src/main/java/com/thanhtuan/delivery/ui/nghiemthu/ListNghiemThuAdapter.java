package com.thanhtuan.delivery.ui.nghiemthu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.Photo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dift.ui.SwipeToAction;

public class ListNghiemThuAdapter extends RecyclerView.Adapter<ListNghiemThuAdapter.NghiemThuViewHolder> {
    private List<Photo> photos;
    private LayoutInflater mLayoutInflater;

    ListNghiemThuAdapter(List<Photo> photos, Context mContext) {
        this.photos = photos;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ListNghiemThuAdapter.NghiemThuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_nghiemthu, parent, false);
        return new ListNghiemThuAdapter.NghiemThuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListNghiemThuAdapter.NghiemThuViewHolder holder, int position) {
        final Photo photo = photos.get(position);

        holder.ibtnIMG.setImageBitmap(photo.getImage());
        holder.txtvLydo.setText(photo.getDescription());
        holder.data = photo;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class NghiemThuViewHolder extends SwipeToAction.ViewHolder<Photo> {
        @BindView(R.id.imgPhoto) ImageView ibtnIMG;
        @BindView(R.id.txtvMoTa2) TextView txtvLydo;
        NghiemThuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
