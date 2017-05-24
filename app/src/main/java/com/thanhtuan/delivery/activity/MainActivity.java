package com.thanhtuan.delivery.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.adapter.ListSaleAdapter;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.Item;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @SuppressLint("SetTextI18n")
    private void addControls() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitleToolbar.setText("Danh sách đơn hàng");

        ListSaleAdapter adapter = new ListSaleAdapter(mItem, this);
        rcvDonHang.setAdapter(adapter);
    }

    private void initData() {
        SharedPreferences pre=getSharedPreferences("MyPre", MODE_PRIVATE);
        String ID = pre.getString("ID", null);

        String API_LISTSALE = ApiHelper.URL + ApiHelper.DOMAIN_LISTSALE + "key=" + ID;
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

                                    mItem.add(item);
                                    addControls();

                                    //RecyclerView scroll vertical
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
                                    rcvDonHang.setLayoutManager(linearLayoutManager);
                                    if (i < listItem.length() - 1){
                                        stopAnim();
                                    }
                                }
                            }else {
                                Toast.makeText(MainActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
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
}
