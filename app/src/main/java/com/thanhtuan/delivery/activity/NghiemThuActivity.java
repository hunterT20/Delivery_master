package com.thanhtuan.delivery.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.adapter.ListNghiemThuAdapter;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.Item;
import com.thanhtuan.delivery.model.Photo;
import com.thanhtuan.delivery.model.SaleReceiptUpdate;
import com.thanhtuan.delivery.model.URL_PhotoUpload;
import com.thanhtuan.delivery.sharePreference.MyShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final static int CODE_PHOTO = 2002;
    private List<Photo> photoList;
    public List<URL_PhotoUpload> url_photoUploads;
    private Boolean flag_back = false;
    private SaleReceiptUpdate saleReceiptUpdate;
    private Bitmap photo_taked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghiem_thu);
        ButterKnife.bind(this);

        addViews();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        final Bitmap bitmap_old = ((BitmapDrawable)ibtnPhoto.getDrawable()).getBitmap();

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BitmapDrawable)ibtnPhoto.getDrawable()).getBitmap() == bitmap_old){
                    Toast.makeText(NghiemThuActivity.this, "Bạn chưa có hình để nghiệm thu!", Toast.LENGTH_SHORT).show();
                }else {
                    if (edtMoTa.getText().toString().length() < 15){
                        Toast.makeText(NghiemThuActivity.this, "Mô tả quá ngắn!", Toast.LENGTH_SHORT).show();
                    }else {
                        String Description = edtMoTa.getText().toString();

                        Photo photo = new Photo();
                        photo.setDescription(Description);
                        photo.setImage(photo_taked);
                        photoList.add(photo);
                        addControls();

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
                        rcvNghiemThu.setLayoutManager(linearLayoutManager);

                        getPhoto_url(photo_taked, Description);

                        edtMoTa.setText("");
                        ibtnPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_white_24dp));
                        cvNghiemThuGONE();
                    }
                }
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
        url_photoUploads = new ArrayList<>();
        saleReceiptUpdate = new SaleReceiptUpdate();
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
                Gson gson = new Gson();
                SharedPreferences pre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
                String json = pre.getString("SaleItem", "");
                Item item1 = gson.fromJson(json, Item.class);

                saleReceiptUpdate.setSaleReceiptId(item1.getSaleReceiptId());
                String ID = pre.getString(MyShare.VALUE_ID, null);
                saleReceiptUpdate.setUrl(url_photoUploads);

                String SaleReceiptUpdate = gson.toJson(saleReceiptUpdate);
                String API_URL = ApiHelper.URL + ApiHelper.DOMAIN_NGHIEMTHU;

                HashMap<String, String> params = new HashMap<>();
                params.put("key", ID);
                params.put("saleReceipt", SaleReceiptUpdate);

                Log.e("params", String.valueOf(params));
                Log.e("SAle", SaleReceiptUpdate);

                JsonObjectRequest request_json = new JsonObjectRequest(API_URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("Result")){
                                        JSONObject jsonObject = response.getJSONObject("Data");
                                    }else {
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
                VolleySingleton.getInstance(this).getRequestQueue().add(request_json);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eventAddPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CODE_PHOTO);
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
        if (requestCode == CODE_PHOTO && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            photo_taked = bitmap;
            ibtnPhoto.setImageBitmap(bitmap);
        }
    }

    private void getPhoto_url(Bitmap bitmap, final String des){
        String base64Photo = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

        Gson gson = new Gson();
        SharedPreferences pre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        String json = pre.getString("SaleItem", "");
        Item item = gson.fromJson(json, Item.class);

        String ID = pre.getString(MyShare.VALUE_ID, null);

        String API_PHOTO = ApiHelper.URL + ApiHelper.DOMAIN_UPLOADIMG;

        HashMap<String, String> params = new HashMap<>();
        params.put("base64Photo", base64Photo);
        params.put("sku", item.getSaleReceiptId());
        params.put("key", ID);

        JsonObjectRequest request_json = new JsonObjectRequest(API_PHOTO, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("Result")){
                                URL_PhotoUpload url = new URL_PhotoUpload();
                                url.setImage(response.getString("Data"));
                                Log.e("data", response.getString("Data"));
                                url.setDescription(des);

                                url_photoUploads.add(url);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        VolleySingleton.getInstance(this).getRequestQueue().add(request_json);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

}
