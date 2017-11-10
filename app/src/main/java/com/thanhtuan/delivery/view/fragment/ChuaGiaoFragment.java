package com.thanhtuan.delivery.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.common.api.Api;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.model.Item_ChuaGiao;
import com.thanhtuan.delivery.util.SharePreferenceUtil;
import com.thanhtuan.delivery.util.AVLoadingUtil;
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

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChuaGiaoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rcvDonHang_ChuaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.avi_loading)   AVLoadingIndicatorView avi_Loading;
    @BindView(R.id.txtvNoItem_ChuaGiao)    TextView txtvNoItem;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private List<Item_ChuaGiao> mItemChuaGiao;
    boolean isRefresh = false;

    public ChuaGiaoFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chua_giao, container, false);
        ButterKnife.bind(this,view);

        swipeRefreshLayout.setOnRefreshListener(this);
        mItemChuaGiao = new ArrayList<>();
        initReCyclerView();

        initData();
        return view;
    }

    private void setListSale() {
        ListSaleAdapter adapter = new ListSaleAdapter(mItemChuaGiao, getActivity());
        rcvDonHang.setAdapter(adapter);
    }

    private void initData() {
        final String Token = SharePreferenceUtil.getValueToken(getActivity());

        txtvNoItem.setVisibility(View.GONE);
        AVLoadingUtil.startAnim(avi_Loading);

        String URL = ApiHelper.ApiListChuaGIao(getActivity());
        JsonRequest.Request(getActivity(), Token, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("Success")){
                        txtvNoItem.setVisibility(View.GONE);
                        JSONArray listItem = response.getJSONArray("Data");

                        for (int i = 0; i < listItem.length(); i++){
                            JSONObject object = (JSONObject) listItem.get(i);

                            Item_ChuaGiao itemChuaGiao = new Item_ChuaGiao();
                            itemChuaGiao.setSaleReceiptId(object.getString("SaleReceiptId"));
                            itemChuaGiao.setCustomerName(object.getString("CustomerName"));
                            itemChuaGiao.setPhoneNumber(object.getString("PhoneNumber"));
                            itemChuaGiao.setAddress(object.getString("AddressFull"));
                            itemChuaGiao.setDistrict(object.getString("District"));
                            itemChuaGiao.setProvince(object.getString("Province"));
                            itemChuaGiao.setQuantity(object.getInt("Quantity"));
                            itemChuaGiao.setPrice(object.getDouble("Price"));
                            itemChuaGiao.setNote(object.getString("Note"));
                            itemChuaGiao.setStatus(object.getInt("Status"));

                            mItemChuaGiao.add(itemChuaGiao);
                        }

                        setListSale();
                        AVLoadingUtil.stopAnim(avi_Loading);
                        swipeRefreshLayout.setRefreshing(false);
                    }else {
                        txtvNoItem.setVisibility(View.VISIBLE);
                        AVLoadingUtil.stopAnim(avi_Loading);
                        isRefresh = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    isRefresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }
            }
        });
    }

    private void initReCyclerView(){
        rcvDonHang.setAdapter(new ListSaleAdapter(mItemChuaGiao,getActivity()));
        rcvDonHang.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvDonHang.setHasFixedSize(true);
    }

    @Override
    public void onRefresh() {
        if(!isRefresh){
            isRefresh = true;
            mItemChuaGiao.clear();
            initData();
        }
    }
}
