package com.thanhtuan.delivery.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.api.ApiListResult;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.data.model.ItemChuaGiao;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChuaGiaoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG =  ChuaGiaoFragment.class.getSimpleName();
    @BindView(R.id.rcvDonHang_ChuaGiao)    RecyclerView rcvDonHang;
    @BindView(R.id.txtvNoItem_ChuaGiao)    TextView txtvNoItem;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private ListSaleAdapter adapter;

    public ChuaGiaoFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chua_giao, container, false);
        ButterKnife.bind(this,view);

        adapter = new ListSaleAdapter(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);
        initReCyclerView();

        initData();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    private void initData() {
        final String Token = SharePreferenceUtil.getValueToken(getActivity());
        String ID = SharePreferenceUtil.getValueId(getActivity());

        txtvNoItem.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        Observable<ApiListResult<ItemChuaGiao>> getItemChuaGiao = ApiUtils.getAPIservices().getItemChuaGiao(Token,ID);

        disposable.add(
                getItemChuaGiao.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiListResult<ItemChuaGiao>>() {
                            @Override
                            public void onNext(ApiListResult<ItemChuaGiao> result) {
                                if (result.getSuccess()) {
                                    txtvNoItem.setVisibility(View.GONE);
                                    List<ItemChuaGiao> itemChuaGiaoList = result.getData();

                                    adapter.addList(itemChuaGiaoList);
                                    rcvDonHang.setAdapter(adapter);
                                }else {
                                    txtvNoItem.setVisibility(View.VISIBLE);
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
                        })
        );
    }

    private void initReCyclerView(){
        rcvDonHang.setAdapter(adapter);
        rcvDonHang.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvDonHang.setHasFixedSize(true);
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        initData();
    }
}
