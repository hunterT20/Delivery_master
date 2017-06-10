package com.thanhtuan.delivery.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Photo;

import java.util.List;

import co.dift.ui.SwipeToAction;

public class ListNghiemThuAdapter extends RecyclerView.Adapter<ListNghiemThuAdapter.NghiemThuViewHolder> {
    private static final String TAG = "ListNghiemThuAdapter";
    private List<Photo> photos;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ListNghiemThuAdapter(List<Photo> photos, Context mContext) {
        this.photos = photos;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ListNghiemThuAdapter.NghiemThuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cardview_nghiemthu, parent, false);
        return new ListNghiemThuAdapter.NghiemThuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListNghiemThuAdapter.NghiemThuViewHolder holder, int position) {
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
        private ImageView ibtnIMG;
        private TextView txtvLydo;
        NghiemThuViewHolder(View itemView) {
            super(itemView);
            ibtnIMG = (ImageView) itemView.findViewById(R.id.imgPhoto);
            txtvLydo = (TextView) itemView.findViewById(R.id.txtvMoTa2);
        }
    }
}
