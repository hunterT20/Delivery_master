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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcvDonHang;
    private List<Item> mItem;
    Toolbar toolbar;
    TextView txtvTitleToolbar;
    ListSaleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItem = new ArrayList<>();
        rcvDonHang = (RecyclerView) findViewById(R.id.rcvDonHang);
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtvTitleToolbar = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitleToolbar.setText("Danh sách đơn hàng");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rcvDonHang.setLayoutManager(llm);
        rcvDonHang.setHasFixedSize(true);
        adapter = new ListSaleAdapter(mItem, this);
        rcvDonHang.setAdapter(adapter);
    }

    private void initData() {
        SharedPreferences pre=getSharedPreferences("ID", MODE_PRIVATE);
        SharedPreferences.Editor edit=pre.edit();
        String ID = pre.getString("ID", null);
        edit.apply();
        String api_listSale = ApiHelper.URL + ApiHelper.DOMAIN_LISTSALE + "key=" + ID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, api_listSale, null,
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
}
