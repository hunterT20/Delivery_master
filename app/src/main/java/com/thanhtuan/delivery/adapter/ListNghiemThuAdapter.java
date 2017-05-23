package com.thanhtuan.delivery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Photo;

import java.util.List;

/**
 * Created by Nusib on 5/23/2017.
 */

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
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cardview_detail, parent, false);
        return new ListNghiemThuAdapter.NghiemThuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListNghiemThuAdapter.NghiemThuViewHolder holder, int position) {
        Photo photo = new Photo();

        holder.ibtnIMG.setImageBitmap(photo.getImage());
        holder.edtLydo.setText(photo.getDescription());
        holder.edtLydo.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class NghiemThuViewHolder extends RecyclerView.ViewHolder {
        private ImageView ibtnIMG;
        private EditText edtLydo;
        NghiemThuViewHolder(View itemView) {
            super(itemView);
            ibtnIMG = (ImageView) itemView.findViewById(R.id.ibtnChooseIMG);
            edtLydo = (EditText) itemView.findViewById(R.id.edtLyDo2);
        }
    }
}
