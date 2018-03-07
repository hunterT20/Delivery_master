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
import android.util.Log;
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
import com.thanhtuan.delivery.data.model.api.ApiResult;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.ui.main.MainActivity;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.model.Photo;
import com.thanhtuan.delivery.data.model.URL_PhotoUpload;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;
import com.thanhtuan.delivery.utils.RecyclerViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dift.ui.SwipeToAction;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NghiemThuActivity extends AppCompatActivity {
    @BindView(R.id.fab_Photo)       FloatingActionButton fab;
    @BindView(R.id.cvNghiemThu)     CardView cvNghiemThu;
    @BindView(R.id.rcvNghiemThu)    RecyclerView rcvNghiemThu;
    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.toolbar_title)    TextView txtvTitle;
    @BindView(R.id.ibtnPhoto)       ImageView ibtnPhoto;
    @BindView(R.id.btnXacNhan)      Button btnXacNhan;
    @BindView(R.id.edtMoTa)         EditText edtMoTa;
    @BindView(R.id.RootLayout)      ConstraintLayout Root;
    @BindView(R.id.btnUpload)       Button btnUpload;

    private static final String TAG = NghiemThuActivity.class.getSimpleName();
    private final static int CODE_PHOTO = 2002;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final String Token = SharePreferenceUtil.getValueToken(this);

    private List<Photo> photoList;
    private ListNghiemThuAdapter adapter;
    private Boolean flag_back = false;
    private Bitmap photo_taked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghiem_thu);
        ButterKnife.bind(this);

        adapter = new ListNghiemThuAdapter(this);
        RecyclerViewUtil.setupRecyclerView(rcvNghiemThu,adapter,this);
        rcvNghiemThu.setAdapter(adapter);
        addViews();
        addEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    @OnClick(R.id.fab_Photo)
    public void onPhotoClick(){
        cvNghiemThuDisplay();
    }

    @OnClick(R.id.ibtnPhoto)
    public void onAddPhotoClick(){
        eventAddPhoto();
    }

    @OnClick(R.id.btnUpload)
    public void onUploadClick(){
        if (photoList.size() == 0){
            showErrorDialog("Vui lòng nghiệm thu trước khi update!");
            return;
        }
        onUpload();
    }

    @OnClick(R.id.btnXacNhan)
    public void onXacNhanClick(){
        final Bitmap bitmap_old = ((BitmapDrawable)ibtnPhoto.getDrawable()).getBitmap();
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
                adapter.addList(photoList);

                btnXacNhan.setText("Loading...");

                getPhotoUrl(photo_taked);

                edtMoTa.setText("");
                ibtnPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_white_24dp));
            }
        }
    }

    private void addEvents() {
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
    }

    private void addViews() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitle.setText("Nghiệm Thu");
        photoList = new ArrayList<>();
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
        HashMap<String,String> params = ApiHelper.paramUpload(this,bitmap);

        Observable<ApiResult<String>> postPhoto = ApiUtils.getAPIservices().postPhoto(Token, params);
        Disposable disposableObserver =
                postPhoto.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiResult<String>>() {
                            @Override
                            public void onNext(ApiResult<String> result) {
                                if (result.getSuccess()){
                                    btnXacNhan.setText("Xác nhận");
                                    cvNghiemThuGONE();
                                    Toast.makeText(NghiemThuActivity.this, "Đã gửi hình lên server!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

        disposable.add(disposableObserver);
    }

    public void onUpload(){
        HashMap<String, String> params = ApiHelper.paramDone(this,"default");

        Observable<ApiResult<String>> done = ApiUtils.getAPIservices().done(Token,params);
        Disposable disposableDone =
                done.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiResult<String>>() {
                            @Override
                            public void onNext(ApiResult<String> result) {
                                if (result.getSuccess()){
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
                                    Snackbar snackbar = Snackbar.make(Root,"Nghiệm thu thất bại!",Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        disposable.add(disposableDone);
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
