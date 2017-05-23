package com.thanhtuan.delivery.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.adapter.ListNghiemThuAdapter;
import com.thanhtuan.delivery.adapter.ListProductAdapter;
import com.thanhtuan.delivery.model.Photo;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NghiemThuFragment extends Fragment {
    @BindView(R.id.rcvNghiemThu)   RecyclerView rcvNghiemThu;
    @BindView(R.id.fabPhoto)
    FloatingActionButton fabPhoto;
    private List<Photo> photoList;
    Bitmap bitmap;

    public NghiemThuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nghiem_thu, container, false);
        ButterKnife.bind(this, view);

        addControls();
        return view;
    }

    private void addControls() {
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventAddPhoto();
            }
        });

        ListNghiemThuAdapter adapter = new ListNghiemThuAdapter(photoList, getActivity());
        rcvNghiemThu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void eventAddPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    private void eventNghiemThuButton(final Bitmap bitmap){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.adapter_cardview_nghiemthu, null);
        ImageView image = (ImageView) mView.findViewById(R.id.ibtnChooseIMG);
        image.setImageBitmap(bitmap);
        final EditText edtLyDo = (EditText) mView.findViewById(R.id.edtLyDo2);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (edtLyDo.getText().length() < 10){
                            Toast.makeText(getActivity(), "Lý do quá ngắn!", Toast.LENGTH_SHORT).show();
                        }else {
                            Photo photo = new Photo();
                            photo.setImage(bitmap);
                            photo.setDescription(edtLyDo.getText().toString());

                            photoList.add(photo);
                        }
                    }
                })
                .setTitle("Nghiệm thu")

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bitmap = (Bitmap) data.getExtras().get("data");
    }
}
