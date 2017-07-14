package com.thanhtuan.delivery.data.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thanhtuan.delivery.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ApiHelper {
    public static String URL = "http://112.78.12.251:12358/api/";
    private static String URL2 = "http://192.168.1.79:8080/api/";
    public static String URL_MAP = "https://maps.googleapis.com/maps/api/";

    public static String DOMAIN_UPLOADIMG = "UploadPhoto/";
    public static String DOMAIN_NGHIEMTHU = "Done/";
    public static String DOMAIN_MAP = "directions/json?";

    public static String ApiLogin(){
        String DOMAIN_LOGIN = "accounts/login";
        return URL2 + DOMAIN_LOGIN;
    }

    public static String ApiListChuaGIao(Context context){
        String DOMAIN_LISTSALE = "salereceipt/employeeId?";
        String PARAM = "EmployeeId=";
        String ID = SharePreferenceUtil.getValueId(context);

        return URL2 + DOMAIN_LISTSALE + PARAM + ID;
    }

    public static String ApiListDaGiao(int pages, String timeBegin, String timeEnd){
        String DOMAIN_DAGIAO = "salereceipt/deliveries/filter?";
        String PARAM1 = "pageNumber=";
        String PARAM2 = "&pageSize=";
        String PARAM3 = "&startDate=";
        String PARAM4 = "&endDate=";

        return URL2 + DOMAIN_DAGIAO + PARAM1 + pages + PARAM2 + 5 +
                PARAM3 + timeBegin + PARAM4 + timeEnd;
    }

    public static String ApiDetail(Context context){
        String DOMAIN_LISTPRODUCT = "salereceipt/detail?";
        String PARAM1 = "EmployeeId=";
        String ID = SharePreferenceUtil.getValueId(context);

        return URL2 + DOMAIN_LISTPRODUCT + PARAM1 + ID;
    }

    public static String ApiAbort(Context context,String saleReceiptId, String Description){
        String DOMAIN_HUY = "Abort?";
        final String PARAM1 = "EmployeeId=";
        final String PARAM2 = "&saleReceiptId=";
        final String PARAM3 = "&description=";

        String ID = SharePreferenceUtil.getValueId(context);
        String description = null;
        try {
            description = URLEncoder.encode(String.valueOf(Description), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return URL + DOMAIN_HUY + PARAM1 + ID
                + PARAM2 + saleReceiptId + PARAM3 + description;
    }

    public static String ApiTime(){
        String DOMAIN_TIME = "salereceipt/salereceipttime";
        return URL2 + DOMAIN_TIME;
    }

    public static HashMap<String, String> paramLoGin(String username, String password){
        HashMap<String, String> params = new HashMap<>();
        params.put("EmployeeId", username);
        params.put("password", password);
        return params;
    }

    public static HashMap<String,String> paramTime(String SaleReceiptId, String Status){
        HashMap<String, String> params = new HashMap<>();
        params.put("SaleReceiptId", SaleReceiptId);
        params.put("Status", Status);

        return params;
    }
}
