package com.thanhtuan.delivery.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment{

    @BindView(R.id.map) MapView mapView;

    private static final String TAG = "LoginAcivity";

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        initData();
        return view;
    }

    private void initData() {
        String PARAM1 = "address=";
        String PARAM2 = "&key=AIzaSyDgVVNJ8rQ3QzMrBbgCzhAQKQBZhjBYCHo";

        Gson gson = new Gson();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("MyPre",MODE_PRIVATE);
        String json = mPrefs.getString("SaleItem", "");
        Item item = gson.fromJson(json, Item.class);

        String param1 = "374 TRẦN HƯNG ĐẠO Q5 TPHCM";
        param1.replaceAll(" ","%20");

        String API_MAP = ApiHelper.URL_MAP + ApiHelper.DOMAIN_MAP + PARAM1 + param1 + PARAM2;
        Log.e(TAG,"API: " + API_MAP);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_MAP, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Result")){

                            }else {

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
}
