package com.thanhtuan.delivery.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.FloatingActionButton;
import com.skyfishjy.library.RippleBackground;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.adapter.ListNghiemThuAdapter;
import com.thanhtuan.delivery.fragment.NghiemThuFragment;
import com.thanhtuan.delivery.model.Photo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NghiemThuActivity extends AppCompatActivity {
    @BindView(R.id.fab)             FloatingActionButton fab;
    @BindView(R.id.cvNghiemThu)     CardView cvNghiemThu;
    @BindView(R.id.rcvNghiemThu)    RecyclerView rcvNghiemThu;
    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.toolbarChildren_title)    TextView txtvTitle;
    @BindView(R.id.ibtnPhoto)       ImageView ibtnPhoto;
    @BindView(R.id.btnXacNhan)      Button btnXacNhan;
    @BindView(R.id.edtMoTa)         EditText edtMoTa;
    private Photo photo;
    private List<Photo> photoList;
    private Boolean flag_back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghiem_thu);
        ButterKnife.bind(this);

        addViews();
        addEvents();
    }

    private void addEvents() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvNghiemThuDisplay();
            }
        });

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

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
                rcvNghiemThu.setLayoutManager(linearLayoutManager);

                edtMoTa.setText("");
                ibtnPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_white_24dp));
                cvNghiemThuGONE();
            }
        });
    }

    private void addViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitle.setText("Nghiệm Thu");
        photoList = new ArrayList<>();
    }

    private void addControls() {
        ListNghiemThuAdapter adapter = new ListNghiemThuAdapter(photoList, this);
        rcvNghiemThu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void cvNghiemThuDisplay(){
        rcvNghiemThu.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        cvNghiemThu.setVisibility(View.VISIBLE);
        flag_back = true;
    }

    private void cvNghiemThuGONE(){
        rcvNghiemThu.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        cvNghiemThu.setVisibility(View.GONE);
        flag_back = false;
    }

    @Override
    public void onBackPressed() {
        if (flag_back) {
            cvNghiemThuGONE();
        } else {
            if (photoList.size() != 0) {
                String mess = "Bạn chưa upload nghiệm thu! Có muốn thoát không?";
                showLocationDialog(mess);
            }
            else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_icon, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (flag_back){
                    cvNghiemThuGONE();
                }else {
                    if (photoList.size() != 0){
                        String mess = "Bạn chưa upload nghiệm thu! Có muốn thoát không?";
                        showLocationDialog(mess);
                    }
                    else {
                        finish();
                    }
                }
                return true;
            case R.id.action_Upload:
                Toast.makeText(this, "Upload coming soon!!!", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eventAddPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    private void showLocationDialog(String mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NghiemThuActivity.this);
        builder.setTitle("Cảnh báo!");
        builder.setMessage(mess);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ibtnPhoto.setImageBitmap(bitmap);
    }
}
