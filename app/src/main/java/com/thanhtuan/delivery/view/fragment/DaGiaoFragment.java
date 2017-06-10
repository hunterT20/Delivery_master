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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.VolleySingleton;
import com.thanhtuan.delivery.model.Item_ChuaGiao;
import com.thanhtuan.delivery.model.Item_DaGiao;
import com.thanhtuan.delivery.sharePreference.MyShare;
import com.thanhtuan.delivery.view.adapter.ListDaGiaoAdapter;
import com.thanhtuan.delivery.view.adapter.ListSaleAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaGiaoFragment extends Fragment {
    @BindView(R.id.rcvDonHang_DaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.avi_loading)   AVLoadingIndicatorView avi_Loading;
    @BindView(R.id.txtvNoItem_DaGiao)    TextView txtvNoItem;

    private List<Item_DaGiao> mItemDaGiao;
    public DaGiaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_da_giao, container, false);
        ButterKnife.bind(this,view);

        mItemDaGiao = new ArrayList<>();
        initData();
        return view;
    }

    private void addControls() {
        ListDaGiaoAdapter adapter = new ListDaGiaoAdapter(mItemDaGiao, getActivity());
        rcvDonHang.setAdapter(adapter);
    }

    private void initData() {
        SharedPreferences MyPre = getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        final String Token = MyPre.getString(MyShare.VALUE_TOKEN, null);

        String PARAM1 = "pageNumber=";
        String PARAM2 = "&pageSize=";
        String PARAM3 = "&startDate=";
        String PARAM4 = "&endDate=";
        String API_LISTSALE = ApiHelper.URL2 + ApiHelper.DOMAIN_DAGIAO + PARAM1 + 1 + PARAM2 + 10 + PARAM3 + "1/2/2017" + PARAM4 +
                "2/2/2017";
        startAnim();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTSALE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success")){
                                txtvNoItem.setVisibility(View.GONE);
                                JSONArray listItem = response.getJSONArray("Data");

                                for (int i = 0; i < listItem.length(); i++){
                                    JSONObject object = (JSONObject) listItem.get(i);

                                    Item_DaGiao itemChuaGiao = new Item_DaGiao();
                                    itemChuaGiao.setSaleReceiptId(object.getString("SaleReceiptId"));
                                    itemChuaGiao.setAddress(object.getString("Address"));
                                    itemChuaGiao.setDistrict(object.getString("District"));
                                    itemChuaGiao.setStatus(object.getString("Status"));

                                    mItemDaGiao.add(itemChuaGiao);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Authorization = new HashMap<>();
                Authorization.put("Authorization", Token);
                return Authorization;
            }
        };

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
