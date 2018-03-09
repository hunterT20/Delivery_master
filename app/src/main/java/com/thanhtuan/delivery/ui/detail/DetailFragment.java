package com.thanhtuan.delivery.ui.detail;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.api.ApiListResult;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.data.model.Product;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;
import com.thanhtuan.delivery.utils.RecyclerViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rcvProduct)    RecyclerView rcvProduct;
    @BindView(R.id.swipe_refresh_layout)    SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = DetailFragment.class.getSimpleName();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private ListProductAdapter adapter;

    public DetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        adapter = new ListProductAdapter(getActivity());
        RecyclerViewUtil.setupRecyclerView(rcvProduct, adapter, getActivity());
        rcvProduct.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
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
        String ID = SharePreferenceUtil.getValueSaleItem(getActivity()).getSaleReceiptId();
        swipeRefreshLayout.setRefreshing(true);
        Observable<ApiListResult<Product>> getListProduct = ApiUtils.getAPIservices().getListProduct(Token, ID);
        Disposable disposableData =
                getListProduct.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiListResult<Product>>() {
                            @Override
                            public void onNext(ApiListResult<Product> result) {
                                List<Product> productList = result.getData();
                                adapter.addList(productList);
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

        disposable.add(disposableData);
    }

    @Override
    public void onRefresh() {
        adapter.reset();
        initData();
    }
}
