package com.thanhtuan.delivery.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.activity.DetailActivity;
import com.thanhtuan.delivery.activity.NghiemThuActivity;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.Item;
import com.thanhtuan.delivery.sharePreference.MyShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private Item item;

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
            @Override
            public void onClick(View v) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + item.getPhoneNumber()));
            startActivity(callIntent);
            }
        });
    }

    private void addViews() {
        btnHuyGiaoHang.setEnabled(true);
        Gson gson = new Gson();
        SharedPreferences mPrefs = getActivity().getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
        String json = mPrefs.getString(MyShare.VALUE_SALEITEM, "");
        item = gson.fromJson(json, Item.class);

        int status = mPrefs.getInt(MyShare.VALUE_STATUS,0);
        if (status == 3) {
            setQuaTrinh(item.getStatus());
        }else {
            setQuaTrinh(status);
        }

        txtvDonHang.setText(item.getSaleReceiptId());
        txtvTenKH.setText(item.getCustomerName());
        txtvAddress.setText(item.getAddress());
        txtvSDT.setText(item.getPhoneNumber());
        txtvTongTien.setText(String.valueOf(item.getPrice()) + " VNĐ");
        if (item.getNote().equals(""))
            txtvNote.setText("Không có ghi chú!");
        else
            txtvNote.setText(item.getNote());
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
        SharedPreferences pre=getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
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

                                                    SharedPreferences pre = getActivity().getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
                                                    SharedPreferences.Editor edit = pre.edit();
                                                    edit.putInt(MyShare.VALUE_STATUS, 3);
                                                    edit.apply();

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
}
