package com.thanhtuan.delivery.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.adapter.ListNghiemThuAdapter;
import com.thanhtuan.delivery.model.Photo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NghiemThuFragment extends Fragment {
    @BindView(R.id.rcvNghiemThu)   RecyclerView rcvNghiemThu;
    @BindView(R.id.ibtnPhoto)      ImageView ibtnPhoto;
    @BindView(R.id.btnXacNhan)     Button btnXacNhan;
    @BindView(R.id.edtMoTa)        EditText edtMoTa;
    Photo photo;
    private List<Photo> photoList;

    public NghiemThuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nghiem_thu, container, false);
        ButterKnife.bind(this, view);

        photoList = new ArrayList<>();
        addEvents();
        return view;
    }

    private void addEvents() {
        ibtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventAddPhoto();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo = new Photo();
                photo.setDescription(edtMoTa.getText().toString());
                photo.setImage(((BitmapDrawable)ibtnPhoto.getDrawable()).getBitmap());
                photoList.add(photo);
                addControls();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rcvNghiemThu.setLayoutManager(linearLayoutManager);

                edtMoTa.setText("");
                ibtnPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_white_48dp));
            }
        });
    }

    private void addControls() {
        ListNghiemThuAdapter adapter = new ListNghiemThuAdapter(photoList, getActivity());
        rcvNghiemThu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void eventAddPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ibtnPhoto.setImageBitmap(bitmap);
    }
}
