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
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.data.model.Item_ChuaGiao;
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

    private Item_ChuaGiao itemChuaGiao;
    public List<URL_PhotoUpload> url_photoUploads;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    String Token;

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
                .setPositiveButton("Xác nhận hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        onAbort(edtLyDo.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

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
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        Log.e(TAG, "onClick: " + "Xác nhận");
                        eventSentTime(String.valueOf(edtTime.getText()), String.valueOf(txtvSDT.getText()));
                        dialogBox.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void onAbort(String lyDo){
        if (lyDo.length() < 10){
            Toast.makeText(getActivity(), "Lý do quá ngắn!", Toast.LENGTH_SHORT).show();
        }
        else {
            final String Token = SharePreferenceUtil.getValueToken(getActivity());
            String URL = ApiHelper.ApiAbort();
            HashMap<String,String> param = ApiHelper.paramAbort(getActivity(),"Default");
            JsonRequest.Request(getActivity(), Token, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("Result")) {
                            JSONObject jsonObject = response.getJSONObject("Data");
                            setQuaTrinh(jsonObject.getInt("Status"));

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Thành công!");
                            builder.setMessage("Đã hủy giao hàng!");

                            String positiveText = getActivity().getString(android.R.string.ok);
                            builder.setPositiveButton(positiveText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((DetailActivity)getActivity()).setIntent();
                                        }
                                    });

                            String negativeText = getActivity().getString(android.R.string.cancel);
                            builder.setNegativeButton(negativeText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void eventSentTime(String Time, String Phone){
        Token = SharePreferenceUtil.getValueToken(getActivity());

        JsonRequest.Request(getActivity(), SharePreferenceUtil.getValueToken(getActivity()), ApiHelper.ApiSentSMS(),
                new JSONObject(ApiHelper.paramSentSMS("01669384803", "30")),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("Success")){
                                Log.e(TAG, "onResponse: " + response);
                                //eventTimeRecord("1");
                            }else {
                                Log.e(TAG, "onResponse: " + response.getString("Message"));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getMessage());
                        }
                    }
                });
    }

    private void eventTimeRecord(final String Status){
        Token = SharePreferenceUtil.getValueToken(getActivity());
        String URL = ApiHelper.ApiTime();

        HashMap<String,String> params = ApiHelper.paramTime(
                itemChuaGiao.getSaleReceiptId(),
                Status
        );

        JsonRequest.Request(getActivity(), Token, URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("Success")){
                        JSONObject jsonObject = response.getJSONArray("Data").getJSONObject(0);
                        setQuaTrinh(jsonObject.getInt("Status"));
                        Log.e(TAG, "onResponse: " + jsonObject.getInt("Status"));
                    }else {
                        Log.e("Error Time","ERR");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCancelText("Không")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
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