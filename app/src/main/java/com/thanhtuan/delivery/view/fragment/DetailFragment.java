package com.thanhtuan.delivery.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.util.AVLoadingUtil;
import com.thanhtuan.delivery.view.adapter.ListProductAdapter;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.model.Product;
import com.thanhtuan.delivery.util.SharePreferenceUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    @BindView(R.id.rcvProduct)    RecyclerView rcvProduct;
    @BindView(R.id.avi_loading)   AVLoadingIndicatorView avi_Loading;
    private List<Product> mProduct;

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
        final String Token = SharePreferenceUtil.getValueToken(getActivity());
        String URL = ApiHelper.ApiDetail(getActivity());
        Log.e("URL detail", URL);
        AVLoadingUtil.startAnim(avi_Loading);

        JsonRequest.Request(getActivity(), Token, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("Success")){
                        JSONArray listProduct = response.getJSONArray("Data");

                        for (int i = 0; i < listProduct.length(); i++){
                            JSONObject object = (JSONObject) listProduct.get(i);

                            Product product = new Product();
                            product.setItemId(object.getString("ItemId"));
                            product.setItemName(object.getString("ItemName"));
                            product.setQuantity(object.getInt("Quantity"));

                            mProduct.add(product);
                        }
                        addControls();

                        //RecyclerView scroll vertical
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        rcvProduct.setLayoutManager(linearLayoutManager);
                        AVLoadingUtil.stopAnim(avi_Loading);
                    }else {
                        Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT).show();
                        AVLoadingUtil.stopAnim(avi_Loading);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
}
