package com.thanhtuan.delivery.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.adapter.ListSaleAdapter;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.Item;
import com.thanhtuan.delivery.sharePreference.MyShare;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rcvDonHang) RecyclerView rcvDonHang;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView txtvTitleToolbar;
    @BindView(R.id.avi_loading)   AVLoadingIndicatorView avi_Loading;

    private List<Item> mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mItem = new ArrayList<>();
        initData();
    }

    private void addControls() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitleToolbar.setText("Danh sách đơn hàng");

        ListSaleAdapter adapter = new ListSaleAdapter(mItem, this);
        rcvDonHang.setAdapter(adapter);
    }

    private void initData() {
        SharedPreferences MyPre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        String ID = MyPre.getString(MyShare.VALUE_ID, null);

        String PARAM = "key=";
        String API_LISTSALE = ApiHelper.URL + ApiHelper.DOMAIN_LISTSALE + PARAM + ID;
        startAnim();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTSALE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Result")){
                                JSONObject data = response.getJSONObject("Data");
                                JSONArray listItem = data.getJSONArray("Items");

                                for (int i = 0; i < listItem.length(); i++){
                                    JSONObject object = (JSONObject) listItem.get(i);

                                    Item item = new Item();
                                    item.setSaleReceiptId(object.getString("SaleReceiptId"));
                                    item.setCustomerName(object.getString("CustomerName"));
                                    item.setPhoneNumber(object.getString("PhoneNumber"));
                                    item.setAddress(object.getString("Address"));
                                    item.setDistrict(object.getString("District"));
                                    item.setProvince(object.getString("Province"));
                                    item.setQuantity(object.getInt("Quantity"));
                                    item.setPrice(object.getDouble("Price"));
                                    item.setNote(object.getString("Note"));
                                    item.setStatus(object.getInt("Status"));

                                    if (item.getStatus() != 0 && item.getStatus() !=3){
                                        Gson gson = new Gson();
                                        SharedPreferences mPrefs = getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                        String json = gson.toJson(item);
                                        prefsEditor.putString(MyShare.VALUE_SALEITEM, json);
                                        prefsEditor.putInt(MyShare.VALUE_STATUS,item.getStatus());
                                        prefsEditor.apply();

                                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    mItem.add(item);
                                }

                                addControls();
                                //RecyclerView scroll vertical
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
                                rcvDonHang.setLayoutManager(linearLayoutManager);
                                stopAnim();
                            }else {
                                show_sweetDialog("Không có đơn hàng nào để giao!!!");
                                stopAnim();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "onErrorResponse: " + error.getMessage());
            }
        });

        VolleySingleton.getInstance(getApplication()).getRequestQueue().add(jsonObjectRequest);
    }

    private void startAnim(){
        avi_Loading.show();
        // or avi.smoothToShow();
    }

    private void stopAnim(){
        avi_Loading.hide();
        // or avi.smoothToHide();
    }

    private void show_sweetDialog(String alert){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Thông báo!")
                .setContentText(alert)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
