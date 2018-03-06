package com.thanhtuan.delivery.ui.detail;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.model.api.ApiResult;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.data.model.ItemChuaGiao;
import com.thanhtuan.delivery.data.model.URL_PhotoUpload;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;
import com.thanhtuan.delivery.ui.nghiemthu.NghiemThuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {
    @BindView(R.id.txtvDonHang)  TextView txtvDonHang;
    @BindView(R.id.txtvQuaTrinh) TextView txtvQuaTrinh;
    @BindView(R.id.txtvTenKH)    TextView txtvTenKH;
    @BindView(R.id.txtvSDT)      TextView txtvSDT;
    @BindView(R.id.txtvTongTien) TextView txtvTongTien;
    @BindView(R.id.txtvAddress)  TextView txtvAddress;
    @BindView(R.id.txtvNote)     TextView txtvNote;
    @BindView(R.id.btnHuyGiaoHang)    Button btnHuyGiaoHang;
    @BindView(R.id.btnGiaoHang)  Button btnGiaoHang;
    @BindView(R.id.fabPhone)     FloatingActionButton fabPhone;

    private ItemChuaGiao itemChuaGiao;
    public List<URL_PhotoUpload> url_photoUploads;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String Token = SharePreferenceUtil.getValueToken(getActivity());
    private CompositeDisposable disposable = new CompositeDisposable();

    public InfoFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);
        url_photoUploads = new ArrayList<>();

        addViews();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    private void addViews() {
        btnHuyGiaoHang.setEnabled(true);
        itemChuaGiao = SharePreferenceUtil.getValueSaleItem(getActivity());

        int status = SharePreferenceUtil.getValueStatus(getActivity());
        setQuaTrinh(status);


        txtvDonHang.setText(itemChuaGiao.getSaleReceiptId());
        txtvTenKH.setText(itemChuaGiao.getCustomerName());
        txtvAddress.setText(itemChuaGiao.getAddress());
        txtvSDT.setText(itemChuaGiao.getPhoneNumber());
        txtvTongTien.setText(String.valueOf(itemChuaGiao.getPrice()) + " VNĐ");
        if (itemChuaGiao.getNote().equals(""))
            txtvNote.setText("Không có ghi chú!");
        else
            txtvNote.setText(itemChuaGiao.getNote());
        eventSentTime("0123","0548");
    }

    @OnClick(R.id.btnHuyGiaoHang)
    public void clickHuy(){
        initDialogHuy();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.fabPhone)
    public void callClick(){
        call_Phone();
    }

    @OnClick(R.id.btnGiaoHang)
    public void giaoHangClick(){
        String status = btnGiaoHang.getText().toString();
        switch (status){
            case "Giao Hàng":
                initDialogSetTime();
                break;
            case "Kết Thúc":
                eventTimeRecord("2");
                break;
            case "Nghiệm Thu":
                Intent intent = new Intent(getActivity(), NghiemThuActivity.class);
                startActivity(intent);
                getActivity().finish();
                eventTimeRecord("3");
                break;
        }
    }

    private void setQuaTrinh(int Status){
        switch (Status){
            case 0:
                txtvQuaTrinh.setText("Đang chờ giao hàng");
                break;
            case 1:
                txtvQuaTrinh.setText("Đang giao hàng");
                SharePreferenceUtil.setValueStatus(getActivity(),Status);
                btnGiaoHang.setText("Kết Thúc");
                break;
            case 2:
                txtvQuaTrinh.setText("Đã giao hàng");
                SharePreferenceUtil.setValueStatus(getActivity(),Status);
                btnGiaoHang.setText("Nghiệm Thu");
                break;
            case 3:
                txtvQuaTrinh.setText("Đang nghiệm thu");
                btnGiaoHang.setText("Nghiệm Thu");
                SharePreferenceUtil.setValueStatus(getActivity(),Status);
                break;
            default:
                txtvQuaTrinh.setText("Không xác định");
                break;
        }
    }

    private void initDialogHuy(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_huy, null);
        final EditText edtLyDo = mView.findViewById(R.id.edtLydo);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Xác nhận hủy", (dialogBox, id) -> onAbort(edtLyDo.getText().toString()))
                .setNegativeButton("Cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void initDialogSetTime(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_set_time, null);
        final EditText edtTime = mView.findViewById(R.id.edtTime);
        edtTime.setText(SharePreferenceUtil.getValueTime(getActivity()));
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Xác nhận", (dialogBox, id) -> {
                    eventSentTime(String.valueOf(edtTime.getText()), String.valueOf(txtvSDT.getText()));
                    dialogBox.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void onAbort(String lyDo){
        if (lyDo.length() < 10){
            Toast.makeText(getActivity(), "Lý do quá ngắn!", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,String> param = ApiHelper.paramAbort(getActivity(),"Default");

            Observable<ApiResult<Integer>> postStatusDelivery = ApiUtils.getAPIservices().huyGiaoHang(Token, param);
            Disposable disposableObserver =
                postStatusDelivery.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiResult<Integer>>() {
                            @Override
                            public void onNext(ApiResult<Integer> result) {
                                if (result.getSuccess()) {
                                    setQuaTrinh(result.getData());
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Thành công!");
                                    builder.setMessage("Đã hủy giao hàng!");

                                    String positiveText = getActivity().getString(android.R.string.ok);
                                    builder.setPositiveButton(positiveText,
                                            (dialog, which) -> ((DetailActivity)getActivity()).setIntent());

                                    String negativeText = getActivity().getString(android.R.string.cancel);
                                    builder.setNegativeButton(negativeText,
                                            (dialog, which) -> dialog.dismiss());

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }else {
                                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            disposable.add(disposableObserver);
        }
    }

    private void eventSentTime(String Time, String Phone){
        HashMap<String,String> param = ApiHelper.paramSentSMS("01669384803", "30");
        Observable<ApiResult<String>> sentSMS = ApiUtils.getAPIservices().sentSMS(Token, param);

        Disposable disposableSMS =
            sentSMS.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ApiResult<String>>() {
                        @Override
                        public void onNext(ApiResult<String> result) {
                            Log.e(TAG, "onNext: " + result.getData());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        disposable.add(disposableSMS);
    }

    private void eventTimeRecord(final String Status){
        HashMap<String,String> params = ApiHelper.paramTime(
                itemChuaGiao.getSaleReceiptId(),
                Status
        );

        Observable<ApiResult<Integer>> timeRecord = ApiUtils.getAPIservices().timeRecord(Token, params);
        Disposable disposableRecord =
            timeRecord.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ApiResult<Integer>>() {
                        @Override
                        public void onNext(ApiResult<Integer> result) {
                            //setQuaTrinh(jsonObject.getInt("Status"));
                            setQuaTrinh(result.getData());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        disposable.add(disposableRecord);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    call_Phone();
                } else {
                    // Permission Denied
                    if (getActivity() == null) return;
                    Toast.makeText(getActivity(), "Quyền gọi điện đã bị từ chối!", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void call_Phone(){
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Yêu cầu")
                        .setContentText("Bạn cần cấp quyền truy gọi điện để chức năng hoạt động!")
                        .setConfirmText("Bật quyền gọi điện")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                            sweetAlertDialog.dismiss();
                        })
                        .setCancelText("Không")
                        .setCancelClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss())
                        .show();
                return;
            }
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + itemChuaGiao.getPhoneNumber()));
        startActivity(callIntent);
    }
}
