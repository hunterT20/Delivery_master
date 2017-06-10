package com.thanhtuan.delivery.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.VolleySingleton;
import com.thanhtuan.delivery.model.Item_ChuaGiao;
import com.thanhtuan.delivery.sharePreference.MyShare;
import com.thanhtuan.delivery.view.activity.MainActivity;
import com.thanhtuan.delivery.view.adapter.ListSaleAdapter;
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
public class ChuaGiaoFragment extends Fragment {
    @BindView(R.id.rcvDonHang_ChuaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.avi_loading)   AVLoadingIndicatorView avi_Loading;
    @BindView(R.id.txtvNoItem_ChuaGiao)    TextView txtvNoItem;

    private List<Item_ChuaGiao> mItemChuaGiao;

    public ChuaGiaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chua_giao, container, false);
        ButterKnife.bind(this,view);

        mItemChuaGiao = new ArrayList<>();

        initData();
        return view;
    }

    private void addControls() {
        ListSaleAdapter adapter = new ListSaleAdapter(mItemChuaGiao, getActivity());
        rcvDonHang.setAdapter(adapter);
    }

    private void initData() {
        SharedPreferences MyPre = getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
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
                                txtvNoItem.setVisibility(View.GONE);
                                JSONObject data = response.getJSONObject("Data");
                                JSONArray listItem = data.getJSONArray("Items");

                                for (int i = 0; i < listItem.length(); i++){
                                    JSONObject object = (JSONObject) listItem.get(i);

                                    Item_ChuaGiao itemChuaGiao = new Item_ChuaGiao();
                                    itemChuaGiao.setSaleReceiptId(object.getString("SaleReceiptId"));
                                    itemChuaGiao.setCustomerName(object.getString("CustomerName"));
                                    itemChuaGiao.setPhoneNumber(object.getString("PhoneNumber"));
                                    itemChuaGiao.setAddress(object.getString("Address"));
                                    itemChuaGiao.setDistrict(object.getString("District"));
                                    itemChuaGiao.setProvince(object.getString("Province"));
                                    itemChuaGiao.setQuantity(object.getInt("Quantity"));
                                    itemChuaGiao.setPrice(object.getDouble("Price"));
                                    itemChuaGiao.setNote(object.getString("Note"));
                                    itemChuaGiao.setStatus(object.getInt("Status"));

                                    if (itemChuaGiao.getStatus() != 0){
                                        Gson gson = new Gson();
                                        SharedPreferences mPrefs = getActivity().getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                        String json = gson.toJson(itemChuaGiao);
                                        prefsEditor.putString(MyShare.VALUE_SALEITEM, json);
                                        prefsEditor.putInt(MyShare.VALUE_STATUS, itemChuaGiao.getStatus());
                                        prefsEditor.apply();

                                        ((MainActivity)getActivity()).Intent_Detail();
                                    }
                                    mItemChuaGiao.add(itemChuaGiao);
                                }

                                addControls();
                                //RecyclerView scroll vertical
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                rcvDonHang.setLayoutManager(linearLayoutManager);
                                stopAnim();
                            }else {
                                txtvNoItem.setVisibility(View.VISIBLE);
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

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonObjectRequest);
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
