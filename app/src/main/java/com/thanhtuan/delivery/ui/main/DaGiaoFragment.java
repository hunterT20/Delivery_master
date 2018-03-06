package com.thanhtuan.delivery.ui.main;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.api.ApiListResult;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.utils.EndlessRecyclerViewScrollListener;
import com.thanhtuan.delivery.data.model.ItemDaGiao;
import com.thanhtuan.delivery.utils.RecyclerViewUtil;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaGiaoFragment extends Fragment implements DatePickerDialog.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = DaGiaoFragment.class.getSimpleName();
    @BindView(R.id.rcvDonHang_DaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.txtvNoItem_DaGiao)    TextView txtvNoItem;
    @BindView(R.id.fabFilter)            FloatingActionButton fabFilter;
    @BindView(R.id.swipe_refresh_layout)    SwipeRefreshLayout swipeRefreshLayout;
    private Button btnBegin, btnEnd;

    private List<ItemDaGiao> mItemDaGiao;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private String beginDate,endDate;
    private ListDaGiaoAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int CURRENT_PAGE = 1;

    private int Flag_Time;

    public DaGiaoFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_da_giao, container, false);
        ButterKnife.bind(this,view);
        swipeRefreshLayout.setOnRefreshListener(this);

        mItemDaGiao = new ArrayList<>();
        assert getActivity() != null;
        adapter = new ListDaGiaoAdapter(getActivity());
        RecyclerViewUtil.setupRecyclerView(rcvDonHang, adapter, getActivity());

        addViews();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    private void addViews() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        if (getActivity() == null) return;
        adapter.addList(getData(getCurrentDate(),getCurrentDate(),CURRENT_PAGE));
        rcvDonHang.setAdapter(adapter);
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

        btnBegin.setOnClickListener(v -> {
            Flag_Time = 0;
            initTimeDialog().show();
        });

        btnEnd.setOnClickListener(v -> {
            Flag_Time = 1;
            initTimeDialog().show();
        });

        AlertDialogFilter(mView);
    }

    private void AlertDialogFilter(View mView){
        assert getActivity() != null;
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Xem thống kê", (dialogBox, id) -> {
                    beginDate = btnBegin.getText().toString();
                    endDate = btnEnd.getText().toString();

                    adapter.clear();
                    adapter.addList(getData(beginDate,endDate,CURRENT_PAGE));

                    LoadMore();
                })
                .setTitle("Chọn thống kê")

                .setNegativeButton("Cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private DatePickerDialog initTimeDialog(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        assert getActivity() != null;
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

    private List<ItemDaGiao> getData(String timeBegin, String timeEnd, int pages){
        final String Token = SharePreferenceUtil.getValueToken(getActivity());

        txtvNoItem.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        Observable<ApiListResult<ItemDaGiao>> getItemDaGiao = ApiUtils.getAPIservices().getItemDaGiao(
          Token, pages, 5, timeBegin, timeEnd
        );

        Disposable disposableObserver =
                getItemDaGiao.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiListResult<ItemDaGiao>>() {
                            @Override
                            public void onNext(ApiListResult<ItemDaGiao> result) {
                                if (result.getSuccess()) {
                                    mItemDaGiao.clear();
                                    mItemDaGiao.addAll(result.getData());
                                }else {
                                    if (mItemDaGiao.size() > 0){
                                        txtvNoItem.setVisibility(View.GONE);
                                    }else {
                                        txtvNoItem.setVisibility(View.VISIBLE);
                                    }
                                    adapter.clear();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onComplete() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

        disposable.add(disposableObserver);

        return mItemDaGiao;
    }

    private void LoadMore(){
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, final RecyclerView view) {
                final int size = adapter.getItemCount();

                view.post(() -> adapter.notifyItemRangeInserted(
                        size,
                        getData(beginDate,endDate,page + 1).size() - 1));
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
        adapter.clear();
        adapter.addList(getData(getCurrentDate(),getCurrentDate(),CURRENT_PAGE));
    }
}
