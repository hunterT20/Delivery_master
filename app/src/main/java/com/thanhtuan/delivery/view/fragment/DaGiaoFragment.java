package com.thanhtuan.delivery.view.fragment;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.android.volley.Response;
import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.interface_delivery.EndlessRecyclerViewScrollListener;
import com.thanhtuan.delivery.interface_delivery.OnGetList;
import com.thanhtuan.delivery.model.Item_DaGiao;
import com.thanhtuan.delivery.util.RecyclerViewUtil;
import com.thanhtuan.delivery.util.AVLoadingUtil;
import com.thanhtuan.delivery.util.SharePreferenceUtil;
import com.thanhtuan.delivery.view.adapter.ListDaGiaoAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaGiaoFragment extends Fragment implements DatePickerDialog.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rcvDonHang_DaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.avi_loading)          AVLoadingIndicatorView avi_Loading;
    @BindView(R.id.txtvNoItem_DaGiao)    TextView txtvNoItem;
    @BindView(R.id.fabFilter)            FloatingActionButton fabFilter;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private Button btnBegin, btnEnd;

    private List<Item_DaGiao> mItemDaGiao;
    private String beginDate,endDate;
    private ListDaGiaoAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int CURRENT_PAGE = 1;

    private int Flag_Time;
    boolean isRefresh = false;

    public DaGiaoFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_da_giao, container, false);
        ButterKnife.bind(this,view);
        swipeRefreshLayout.setOnRefreshListener(this);

        mItemDaGiao = new ArrayList<>();
        RecyclerViewUtil.setupRecyclerView(rcvDonHang,new ListDaGiaoAdapter(mItemDaGiao,getActivity()),getActivity());

        addViews();
        return view;
    }

    private void addViews() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        if (getActivity() == null) return;

        getData(getCurrentDate(),getCurrentDate(),CURRENT_PAGE, mItemDaGiao, new OnGetList() {
            @Override
            public void getList(List<Item_DaGiao> itemDaGiaos) {
                adapter = new ListDaGiaoAdapter(itemDaGiaos, getActivity());
                rcvDonHang.setAdapter(adapter);
            }
        });
    }

    @OnClick(R.id.fabFilter)
    public void fabClick(){
        initDialog();
    }

    private void initDialog(){
        if (getActivity() == null) return;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_filter, null);
        btnBegin = mView.findViewById(R.id.btnBegin);
        btnEnd = mView.findViewById(R.id.btnend);

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

        AlertDialogFilter(mView);
    }

    private void AlertDialogFilter(View mView){
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Xem thống kê", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        beginDate = btnBegin.getText().toString();
                        endDate = btnEnd.getText().toString();

                        mItemDaGiao.clear();

                        getData(beginDate,endDate,CURRENT_PAGE, mItemDaGiao, new OnGetList() {
                            @Override
                            public void getList(List<Item_DaGiao> itemDaGiaos) {
                                adapter = new ListDaGiaoAdapter(itemDaGiaos, getActivity());
                                rcvDonHang.setAdapter(adapter);
                            }
                        });

                        LoadMore();
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

    private void getData(String timeBegin, String timeEnd,int pages,
                         final List<Item_DaGiao> list, final OnGetList onGetList){
        final String Token = SharePreferenceUtil.getValueToken(getActivity());

        txtvNoItem.setVisibility(View.GONE);
        AVLoadingUtil.startAnim(avi_Loading);

        String URL = ApiHelper.ApiListDaGiao(pages,timeBegin,timeEnd);

        JsonRequest.Request(getActivity(), Token, URL, null, new Response.Listener<JSONObject>() {
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

                            list.add(itemChuaGiao);
                        }
                        onGetList.getList(list);
                        AVLoadingUtil.stopAnim(avi_Loading);
                        swipeRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }else {
                        if (mItemDaGiao.size() > 0){
                            txtvNoItem.setVisibility(View.GONE);
                        }else {
                            txtvNoItem.setVisibility(View.VISIBLE);
                        }
                        list.clear();
                        AVLoadingUtil.stopAnim(avi_Loading);
                        swipeRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                }
            }
        });
    }

    private void LoadMore(){
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, final RecyclerView view) {
                final List<Item_DaGiao> loadMore = new ArrayList<>();
                getData(beginDate,endDate,page + 1, loadMore, new OnGetList() {
                    @Override
                    public void getList(List<Item_DaGiao> itemDaGiaos) {
                        final int size = adapter.getItemCount();
                        mItemDaGiao.addAll(loadMore);

                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemRangeInserted(size, mItemDaGiao.size() - 1);
                            }
                        });
                    }
                });
            }
        };
        rcvDonHang.addOnScrollListener(scrollListener);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        if (Flag_Time ==0){
            btnBegin.setText(month + "/" + dayOfMonth + "/" + year);
        }else {
            btnEnd.setText(month + "/" + dayOfMonth + "/" + year);
        }
    }

    @Override
    public void onRefresh() {
        if(!isRefresh){
            isRefresh = true;
            mItemDaGiao.clear();
            getData(getCurrentDate(),getCurrentDate(),CURRENT_PAGE, mItemDaGiao, new OnGetList() {
                @Override
                public void getList(List<Item_DaGiao> itemDaGiaos) {
                    adapter = new ListDaGiaoAdapter(itemDaGiaos, getActivity());
                    rcvDonHang.setAdapter(adapter);
                }
            });
        }
    }
}
