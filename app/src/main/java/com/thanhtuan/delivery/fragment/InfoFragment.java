package com.thanhtuan.delivery.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.model.Item;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.BindViews;
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
        return view;
    }

    private void addViews() {
        Gson gson = new Gson();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("MyPre",MODE_PRIVATE);
        String json = mPrefs.getString("SaleItem", "");
        Item item = gson.fromJson(json, Item.class);
        switch (item.getStatus()){
            case 0:
                txtvQuaTrinh.setText("Đang chờ giao hàng");
                break;
            case 1:
                txtvQuaTrinh.setText("Đang giao hàng");
                break;
            case 2:
                txtvQuaTrinh.setText("Đã giao hàng");
                break;
            case 3:
                txtvQuaTrinh.setText("Hủy giao hàng");
                break;
            case 4:
                txtvQuaTrinh.setText("Kết thúc");
                break;
            default:
                txtvQuaTrinh.setText("Không xác định");
                break;
        }
        txtvDonHang.setText(item.getSaleReceiptId());
        txtvTenKH.setText(item.getCustomerName());
        txtvAddress.setText(item.getAddress());
        txtvSDT.setText(item.getPhoneNumber());
        txtvTongTien.setText(String.valueOf(item.getPrice()));
        txtvNote.setText(item.getNote());
    }
}
