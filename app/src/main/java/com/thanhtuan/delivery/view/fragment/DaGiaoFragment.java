package com.thanhtuan.delivery.view.fragment;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.VolleySingleton;
import com.thanhtuan.delivery.model.Item_DaGiao;
import com.thanhtuan.delivery.sharePreference.MyShare;
import com.thanhtuan.delivery.view.adapter.ListDaGiaoAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaGiaoFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    @BindView(R.id.rcvDonHang_DaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.avi_loading)          AVLoadingIndicatorView avi_Loading;
    @BindView(R.id.txtvNoItem_DaGiao)    TextView txtvNoItem;
    @BindView(R.id.fabFilter)            FloatingActionButton fabFilter;
    private Button btnBegin, btnEnd;

    private List<Item_DaGiao> mItemDaGiao;

    private int Flag_Time;

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
        initReCyclerView();
        initData(getCurrentDate(),getCurrentDate());
        addEvents();
        return view;
    }

    private void addEvents() {
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });
    }

    private void addControls() {
        if (getActivity() == null){
            return;
        }
        ListDaGiaoAdapter adapter = new ListDaGiaoAdapter(mItemDaGiao, getActivity());
        rcvDonHang.setAdapter(adapter);
    }

    private void initDialog(){
        if (getActivity() == null) return;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_filter, null);
        btnBegin = (Button) mView.findViewById(R.id.btnBegin);
        btnEnd = (Button) mView.findViewById(R.id.btnend);

        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_Time = 0;
                initTimeDialog().show();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_Time = 1;
                initTimeDialog().show();
            }
        });

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Xem thống kê", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String beginDate = btnBegin.getText().toString();
                        String endDate = btnEnd.getText().toString();

                        mItemDaGiao.clear();

                        initData(beginDate,endDate);
                    }
                })
                .setTitle("Chọn thống kê")

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private DatePickerDialog initTimeDialog(){
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(
                getActivity(), this, year, month, day);
    }

    private String getCurrentDate(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return  month + "/" + day + "/" + year;
    }

    private void initData(String timeBegin, String timeEnd) {
        SharedPreferences MyPre = getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        final String Token = MyPre.getString(MyShare.VALUE_TOKEN, null);

        String PARAM1 = "pageNumber=";
        String PARAM2 = "&pageSize=";
        String PARAM3 = "&startDate=";
        String PARAM4 = "&endDate=";
        String API_LISTSALE = ApiHelper.URL2 + ApiHelper.DOMAIN_DAGIAO + PARAM1 + 1 + PARAM2 + 10 + PARAM3 + timeBegin + PARAM4 +
                timeEnd;

        Log.e("API", API_LISTSALE);

        txtvNoItem.setVisibility(View.GONE);
        startAnim();

        getData(Token,API_LISTSALE);
    }

    private void getData(final String Token, String API_LISTSALE){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTSALE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success")){
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
                                stopAnim();
                            }else {
                                txtvNoItem.setVisibility(View.VISIBLE);
                                mItemDaGiao.clear();
                                addControls();
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

    private void initReCyclerView(){
        rcvDonHang.setAdapter(new ListDaGiaoAdapter(mItemDaGiao,getActivity()));
        rcvDonHang.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvDonHang.setHasFixedSize(true);
    }

    private void startAnim(){
        Log.e(TAG, "startAnim: ");
        //avi_Loading.show();
        avi_Loading.smoothToShow();
    }

    private void stopAnim(){
        Log.e(TAG, "stopAnim: ");
        //avi_Loading.hide();
        avi_Loading.smoothToHide();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        if (Flag_Time ==0){
            btnBegin.setText(month + "/" + dayOfMonth + "/" + year);
        }else {
            btnEnd.setText(month + "/" + dayOfMonth + "/" + year);
        }
    }
}
