package com.thanhtuan.delivery.data.remote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thanhtuan.delivery.model.Item_DaGiao;
import com.thanhtuan.delivery.util.AVLoadingUtil;
import com.thanhtuan.delivery.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiHelper {
    public static String URL = "http://112.78.12.251:12358/api/";
    public static String URL2 = "http://192.168.1.79:8080/api/";
    public static String URL_MAP = "https://maps.googleapis.com/maps/api/";

    public static String DOMAIN_DAGIAO = "salereceipt/deliveries/filter?";
    private static String DOMAIN_LOGIN = "accounts/login";
    public static String DOMAIN_LISTSALE= "salereceipt/employeeId?";
    public static String DOMAIN_LISTPRODUCT= "GetListProduct?";
    public static String DOMAIN_HUY = "Abort?";
    public static String DOMAIN_START = "Start?";
    public static String DOMAIN_END = "End?";
    public static String DOMAIN_UPLOADIMG = "UploadPhoto/";
    public static String DOMAIN_NGHIEMTHU = "Done/";
    public static String DOMAIN_MAP = "directions/json?";
    public static String DOMAIN_TIME = "salereceipt/salereceipttime";

    public static void LOGIN(Context context, HashMap<String,String> params, Response.Listener<JSONObject> objectListener){
        String API_LOGIN = ApiHelper.URL2 + ApiHelper.DOMAIN_LOGIN;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(API_LOGIN, new JSONObject(params),
                objectListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "onErrorResponse: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }

    public static void GETLIST_CHUAGIAO(Context context, String ID, final String Token, Response.Listener<JSONObject> objectListener){
        String PARAM = "EmployeeId=";
        String API_LISTSALE = URL2 + DOMAIN_LISTSALE + PARAM + ID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTSALE, null, objectListener,
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Authorization = new HashMap<>();
                Authorization.put("Authorization", Token);
                return Authorization;
            }
        };

        VolleySingleton.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }

    public static void GETLIST_DAGIAO(Context context, int pages, String timeBegin, String timeEnd,
                                      Response.Listener<JSONObject> objectListener){
        final String Token = SharePreferenceUtil.getValueToken(context);

        String PARAM1 = "pageNumber=";
        String PARAM2 = "&pageSize=";
        String PARAM3 = "&startDate=";
        String PARAM4 = "&endDate=";
        String API_LISTSALE = ApiHelper.URL2 + ApiHelper.DOMAIN_DAGIAO + PARAM1 + pages + PARAM2 + 5 +
                PARAM3 + timeBegin + PARAM4 + timeEnd;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LISTSALE, null,
               objectListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Authorization = new HashMap<>();
                Authorization.put("Authorization", Token);
                return Authorization;
            }
        };

        VolleySingleton.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }
}
