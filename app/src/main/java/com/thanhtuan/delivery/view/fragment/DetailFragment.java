package com.thanhtuan.delivery.view.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.view.adapter.ListProductAdapter;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.VolleySingleton;
import com.thanhtuan.delivery.model.Item_ChuaGiao;
import com.thanhtuan.delivery.model.Product;
import com.thanhtuan.delivery.sharePreference.MyShare;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    @BindView(R.id.rcvProduct)    RecyclerView rcvProduct;
    @BindView(R.id.avi_loading)   AVLoadingIndicatorView avi_Loading;
    private List<Product> mProduct;
    private static Toast toast;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        mProduct = new ArrayList<>();
        initReCyclerView();
        initData();
        return view;
    }

    private void initData() {
        String PARAM1 = "key=";
        String PARAM2 = "&SaleReceiptId=";

        Gson gson = new Gson();
        SharedPreferences pre=getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        String ID = pre.getString(MyShare.VALUE_ID, null);

        String json = pre.getString("SaleItem", "");
        Item_ChuaGiao itemChuaGiao = gson.fromJson(json, Item_ChuaGiao.class);

        String API_LISTPRODUCT = ApiHelper.URL + ApiHelper.DOMAIN_LISTPRODUCT + PARAM1 + ID + PARAM2 + itemChuaGiao.getSaleReceiptId();
        startAnim();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTPRODUCT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Result")){
                                JSONObject data = response.getJSONObject("Data");
                                JSONArray listProduct = data.getJSONArray("Items");

                                for (int i = 0; i < listProduct.length(); i++){
                                    JSONObject object = (JSONObject) listProduct.get(i);

                                    Product product = new Product();
                                    product.setItemId(object.getString("ItemId"));
                                    product.setSKU(object.getString("SKU"));
                                    product.setPrice(object.getDouble("Price"));
                                    product.setQuantity(object.getInt("Quantity"));
                                    product.setStatus(object.getInt("Status"));

                                    mProduct.add(product);
                                }
                                addControls();

                                //RecyclerView scroll vertical
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                rcvProduct.setLayoutManager(linearLayoutManager);
                                stopAnim();
                            }else {
                                if (toast != null)
                                    toast.cancel();
                                toast = Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT);
                                toast.show();
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

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonObjectRequest);
    }
    private void addControls() {
        if(getActivity() == null) return;
        ListProductAdapter adapter = new ListProductAdapter(mProduct, getActivity());
        rcvProduct.setAdapter(adapter);
    }

    private void initReCyclerView(){
        rcvProduct.setAdapter(new ListProductAdapter(mProduct,getActivity()));
        rcvProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvProduct.setHasFixedSize(true);
    }

    private void startAnim(){
        avi_Loading.show();
    }

    private void stopAnim(){
        avi_Loading.hide();
    }
}
