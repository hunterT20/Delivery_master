package com.thanhtuan.delivery.ui.nghiemthu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.ui.main.MainActivity;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.model.Photo;
import com.thanhtuan.delivery.data.model.URL_PhotoUpload;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dift.ui.SwipeToAction;

public class NghiemThuActivity extends AppCompatActivity {
    @BindView(R.id.fab)             FloatingActionButton fab;
    @BindView(R.id.cvNghiemThu)     CardView cvNghiemThu;
    @BindView(R.id.rcvNghiemThu)    RecyclerView rcvNghiemThu;
    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.toolbar_title)    TextView txtvTitle;
    @BindView(R.id.ibtnPhoto)       ImageView ibtnPhoto;
    @BindView(R.id.btnXacNhan)      Button btnXacNhan;
    @BindView(R.id.edtMoTa)         EditText edtMoTa;
    @BindView(R.id.RootLayout)      ConstraintLayout Root;
    @BindView(R.id.btnUpload)       Button btnUpload;

    private final static int CODE_PHOTO = 2002;
    private List<Photo> photoList;
    private ListNghiemThuAdapter adapter;
    public List<URL_PhotoUpload> url_photoUploads;
    private Boolean flag_back = false;
    private Bitmap photo_taked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghiem_thu);
        ButterKnife.bind(this);

        addViews();
        addEvents();
    }

    private void addEvents() {
        fab.setOnClickListener(v -> cvNghiemThuDisplay());

        ibtnPhoto.setOnClickListener(v -> eventAddPhoto());

        final Bitmap bitmap_old = ((BitmapDrawable)ibtnPhoto.getDrawable()).getBitmap();

        btnXacNhan.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(edtMoTa.getWindowToken(), 0);

            if (((BitmapDrawable)ibtnPhoto.getDrawable()).getBitmap() == bitmap_old){
                Snackbar snackbar = Snackbar.make(Root,"Bạn chưa có hình để nghiệm thu!",Snackbar.LENGTH_LONG).setAction("OK", v1 -> {
                });
                snackbar.show();
            }else {
                if (edtMoTa.getText().toString().length() < 15){
                    Snackbar snackbar = Snackbar.make(Root,"Mô tả quá ngắn!",Snackbar.LENGTH_LONG).setAction("OK", v12 -> {
                    });
                    snackbar.show();
                }else {
                    String Description = edtMoTa.getText().toString();

                    Photo photo = new Photo();
                    photo.setDescription(Description);
                    photo.setImage(photo_taked);
                    photoList.add(photo);
                    addControls();

                    btnXacNhan.setText("Loading...");

                    getPhotoUrl(photo_taked);

                    edtMoTa.setText("");
                    ibtnPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_white_24dp));
                }
            }
        });

        new SwipeToAction(rcvNghiemThu, new SwipeToAction.SwipeListener<Photo>() {

            @Override
            public boolean swipeLeft(Photo itemData) {
                removeNghiemThu(itemData);
                return true;
            }

            @Override
            public boolean swipeRight(Photo itemData) {
                return false;
            }

            @Override
            public void onClick(Photo itemData) {

            }

            @Override
            public void onLongClick(Photo itemData) {

            }
        });

        btnUpload.setOnClickListener(v -> {
            if (photoList.size() == 0){
                showErrorDialog("Vui lòng nghiệm thu trước khi update!");
                return;
            }
            onUpload();
        });
    }

    private void addViews() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitle.setText("Nghiệm Thu");
        photoList = new ArrayList<>();
        url_photoUploads = new ArrayList<>();
    }

    private void addControls() {
        adapter = new ListNghiemThuAdapter(photoList, this);
        rcvNghiemThu.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NghiemThuActivity.this, LinearLayoutManager.VERTICAL, false);
        rcvNghiemThu.setLayoutManager(linearLayoutManager);
    }

    private void cvNghiemThuDisplay(){
        rcvNghiemThu.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);
        cvNghiemThu.setVisibility(View.VISIBLE);
        flag_back = true;
    }

    private void cvNghiemThuGONE(){
        rcvNghiemThu.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.VISIBLE);
        cvNghiemThu.setVisibility(View.GONE);
        flag_back = false;
    }

    @Override
    public void onBackPressed() {
        if (flag_back) {
            cvNghiemThuGONE();
        } else {
            if (photoList.size() != 0) {
                showWarnningActionUploadDialog();
            }
            else {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (flag_back){
                    cvNghiemThuGONE();
                }else {
                    if (photoList.size() != 0){
                        showWarnningActionUploadDialog();
                    }
                    else {
                        finish();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eventAddPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CODE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_PHOTO && resultCode == RESULT_OK){
            assert data.getExtras() != null;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            photo_taked = bitmap;
            ibtnPhoto.setImageBitmap(bitmap);
        }
    }

    public void getPhotoUrl(Bitmap bitmap){
        String API_PHOTO = ApiHelper.ApiUpload();
        HashMap<String,String> params = ApiHelper.paramUpload(this,bitmap);
        final String Token = SharePreferenceUtil.getValueToken(this);

        JsonRequest.Request(this, Token, API_PHOTO, new JSONObject(params), response -> {
        try {
            if (response.getBoolean("Success")){
                btnXacNhan.setText("Xác nhận");
                cvNghiemThuGONE();
                Toast.makeText(NghiemThuActivity.this, "Đã gửi hình lên server!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        });
    }

    public void onUpload(){
        HashMap<String, String> params = ApiHelper.paramDone(this,"default");
        String URL = ApiHelper.ApiDone();
        final String Token = SharePreferenceUtil.getValueToken(this);

        JsonRequest.Request(this, Token, URL, new JSONObject(params), response -> {
            try {
                if (response.getBoolean("Success")){
                    SharePreferenceUtil.Clean(NghiemThuActivity.this);

                    AlertDialog.Builder builder = new AlertDialog.Builder(NghiemThuActivity.this);
                    String positiveText = NghiemThuActivity.this.getString(android.R.string.ok);
                    builder.setCancelable(false)
                            .setPositiveButton(positiveText, (dialog, which) -> {
                                startActivity(new Intent(NghiemThuActivity.this,MainActivity.class));
                                finish();
                            })
                            .setMessage("Nghiệm thu thành công!")
                            .setTitle("Thành công!");

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {
                    Snackbar snackbar = Snackbar.make(Root,String.valueOf(response.getBoolean("Nghiệm thu thất bại!")),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void showWarnningActionUploadDialog() {
        String mess = "Bạn chưa upload nghiệm thu! Có muốn thoát không?";
        AlertDialog.Builder builder = new AlertDialog.Builder(NghiemThuActivity.this);

        String positiveText = NghiemThuActivity.this.getString(android.R.string.ok);
        builder.setCancelable(false)
                .setPositiveButton(positiveText, (dialog, which) -> finish())
                .setNegativeButton(NghiemThuActivity.this.getText(android.R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss())
                .setMessage(mess)
                .setTitle("Cảnh báo!");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NghiemThuActivity.this);

        String positiveText = NghiemThuActivity.this.getString(android.R.string.ok);
        builder.setCancelable(false)
                .setPositiveButton(positiveText, (dialog, which) -> dialog.dismiss())
                .setMessage(s)
                .setTitle("Cảnh báo!");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeNghiemThu(Photo photo){
        int pos = photoList.indexOf(photo);
        photoList.remove(pos);
        adapter.notifyItemRemoved(pos);
    }
}
