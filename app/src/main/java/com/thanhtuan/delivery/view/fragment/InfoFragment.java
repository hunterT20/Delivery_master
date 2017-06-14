package com.thanhtuan.delivery.view.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.rey.material.widget.FloatingActionButton;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Item_ChuaGiao;
import com.thanhtuan.delivery.view.activity.DetailActivity;
import com.thanhtuan.delivery.view.activity.NghiemThuActivity;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.VolleySingleton;
import com.thanhtuan.delivery.SharePreference.MyShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

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
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private int status;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);

        addViews();
        addEvents();
        return view;
    }

    private void addEvents() {
        btnHuyGiaoHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHuyGiaoHang();
            }
        });

        btnGiaoHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = btnGiaoHang.getText().toString();
                switch (status){
                    case "Giao Hàng":
                        eventGiaoHang(ApiHelper.DOMAIN_START,"saleReceiptId");
                        break;
                    case "Kết Thúc":
                        eventGiaoHang(ApiHelper.DOMAIN_END, "saleReceiptId");
                        break;
                    case "Nghiệm Thu":
                        Intent intent = new Intent(getActivity(), NghiemThuActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        fabPhone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                call_Phone();
            }
        });
    }

    private void addViews() {
        btnHuyGiaoHang.setEnabled(true);
        Gson gson = new Gson();
        SharedPreferences mPrefs = getActivity().getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
        String json = mPrefs.getString(MyShare.VALUE_SALEITEM, "");
        itemChuaGiao = gson.fromJson(json, Item_ChuaGiao.class);

        int status = mPrefs.getInt(MyShare.VALUE_STATUS,0);
        if (status == 3) {
            setQuaTrinh(itemChuaGiao.getStatus());
        }else {
            setQuaTrinh(status);
        }

        txtvDonHang.setText(itemChuaGiao.getSaleReceiptId());
        txtvTenKH.setText(itemChuaGiao.getCustomerName());
        txtvAddress.setText(itemChuaGiao.getAddress());
        txtvSDT.setText(itemChuaGiao.getPhoneNumber());
        txtvTongTien.setText(String.valueOf(itemChuaGiao.getPrice()) + " VNĐ");
        if (itemChuaGiao.getNote().equals(""))
            txtvNote.setText("Không có ghi chú!");
        else
            txtvNote.setText(itemChuaGiao.getNote());
    }

    private void setQuaTrinh(int Status){
        SharedPreferences pre = getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = pre.edit();
        switch (Status){
            case 0:
                txtvQuaTrinh.setText("Đang chờ giao hàng");
                break;
            case 1:
                txtvQuaTrinh.setText("Đang giao hàng");
                prefsEditor.putInt(MyShare.VALUE_STATUS,Status);
                btnGiaoHang.setText("Kết Thúc");
                break;
            case 2:
                txtvQuaTrinh.setText("Hoàn tất giao hàng");
                prefsEditor.putInt(MyShare.VALUE_STATUS,Status);
                btnGiaoHang.setEnabled(false);
                btnHuyGiaoHang.setEnabled(false);
                break;
            case 3:
                txtvQuaTrinh.setText("Hủy giao hàng");
                prefsEditor.putInt(MyShare.VALUE_STATUS,Status);
                break;
            case 4:
                txtvQuaTrinh.setText("Đã Giao Hàng");
                prefsEditor.putInt(MyShare.VALUE_STATUS,Status);
                btnGiaoHang.setText("Nghiệm Thu");
                break;
            default:
                txtvQuaTrinh.setText("Không xác định");
                break;
        }
        prefsEditor.apply();
    }

    private void eventGiaoHang(String domain, String param){
        final SharedPreferences pre=getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        String ID = pre.getString(MyShare.VALUE_ID, null);

        String API_START = ApiHelper.URL + domain + "key=" + ID
                + "&" + param + "=" + txtvDonHang.getText();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_START, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("Result")) {
                                JSONObject jsonObject = response.getJSONObject("Data");
                                setQuaTrinh(jsonObject.getInt("Status"));
                                status = jsonObject.getInt("Status");

                                SharedPreferences.Editor edit = pre.edit();
                                edit.putInt(MyShare.VALUE_STATUS, status);
                                edit.apply();
                            } else {
                                Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT).show();
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

    private void eventHuyGiaoHang(){
        final String PARAM1 = "key=";
        final String PARAM2 = "&saleReceiptId=";
        final String PARAM3 = "&description=";

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_huy, null);
        final EditText edtLyDo = (EditText) mView.findViewById(R.id.edtLydo);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Xác nhận hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (edtLyDo.getText().length() < 10){
                            Toast.makeText(getActivity(), "Lý do quá ngắn!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            SharedPreferences pre=getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
                            String ID = pre.getString(MyShare.VALUE_ID, null);
                            String description = null;
                            try {
                                description = URLEncoder.encode(String.valueOf(edtLyDo.getText()), "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            String API_LISTSALE = ApiHelper.URL + ApiHelper.DOMAIN_HUY + PARAM1 + ID
                                    + PARAM2 + txtvDonHang.getText() + PARAM3 + description;

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTSALE, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if (response.getBoolean("Result")) {
                                                    JSONObject jsonObject = response.getJSONObject("Data");
                                                    setQuaTrinh(jsonObject.getInt("Status"));
                                                    Toast.makeText(getActivity(), "Đã hủy giao hàng!", Toast.LENGTH_SHORT).show();

                                                    ((DetailActivity)getActivity()).setEventHuy();
                                                } else {
                                                    Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT).show();
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
                })
                .setTitle("Lý Do Hủy")

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
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
