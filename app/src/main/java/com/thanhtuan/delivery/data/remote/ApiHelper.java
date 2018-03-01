package com.thanhtuan.delivery.data.remote;

import android.content.Context;
import android.graphics.Bitmap;

import com.thanhtuan.delivery.data.model.Item_ChuaGiao;
import com.thanhtuan.delivery.utils.EncodeBitmapUtil;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class ApiHelper {
    private static String URL2 = "http://dmclwspl_2015.dienmaycholon.com.vn:12341/api/";
    private static String URL_MAP = "https://maps.googleapis.com/maps/api/";

    private static String DOMAIN_MAP = "directions/json?";

    public static String ApiLogin(){
        String DOMAIN_LOGIN = "accounts/login";
        return URL2 + DOMAIN_LOGIN;
    }

    public static String ApiVersion(){
        String DOMAIN_VERSION = "salereceipt/version/ANDROID";
        return URL2 + DOMAIN_VERSION;
    }

    public static String ApiUpload(){
        String DOMAIN_UPLOADIMG = "salereceipt/photoupload";
        return URL2 + DOMAIN_UPLOADIMG;
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
        String DOMAIN_LISTPRODUCT = "salereceipt/detail/";
        Item_ChuaGiao item = SharePreferenceUtil.getValueSaleItem(context);
        String ID = item.getSaleReceiptId();

        return URL2 + DOMAIN_LISTPRODUCT + ID;
    }

    public static String ApiAbort(){
        String DOMAIN_HUY = "salereceipt/salereceiptabort";
        return URL2 + DOMAIN_HUY;
    }

    public static String ApiTime(){
        String DOMAIN_TIME = "salereceipt/salereceipttime";
        return URL2 + DOMAIN_TIME;
    }

    public static String ApiMap(Context context, double latitudeCurrent, double longitudeCurrent){
        String PARAM1 = "origin=";
        String PARAM2 = "&destination=";
        String PARAM3 = "&language=";
        String PARAM4 = "&key=";
        String key = "AIzaSyCueeDritXwUW37E3jH897o9iBHyIMpseE";

        Item_ChuaGiao itemChuaGiao = SharePreferenceUtil.getValueSaleItem(context);
        String address = itemChuaGiao.getAddress();
        try {
            address = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ApiHelper.URL_MAP + ApiHelper.DOMAIN_MAP + PARAM1 + latitudeCurrent + "," + longitudeCurrent +
                PARAM2 + address + PARAM3 + "vi" + PARAM4 + key;
    }

    public static String ApiDone(){
        String DOMAIN_NGHIEMTHU = "salereceipt/salereceiptdone";
        return URL2 + DOMAIN_NGHIEMTHU;
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

    public static HashMap<String,String> paramAbort(Context context, String Description){
        String description = null;
        try {
            description = URLEncoder.encode(String.valueOf(Description), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Item_ChuaGiao itemChuaGiao1 = SharePreferenceUtil.getValueSaleItem(context);
        String ID = SharePreferenceUtil.getValueId(context);
        String distance = SharePreferenceUtil.getValueDistance(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("employeeId", ID);
        params.put("saleReceiptId", itemChuaGiao1.getSaleReceiptId());
        params.put("Distance", distance);
        params.put("Description",description);

        return params;
    }

    public static HashMap<String,String> paramDone(Context context, String description){
        Item_ChuaGiao itemChuaGiao1 = SharePreferenceUtil.getValueSaleItem(context);

        String ID = SharePreferenceUtil.getValueId(context);
        String distance = SharePreferenceUtil.getValueDistance(context);

        HashMap<String, String> params = new HashMap<>();
        params.put("employeeId", ID);
        params.put("saleReceiptId", itemChuaGiao1.getSaleReceiptId());
        params.put("Distance", distance);
        params.put("Description",description);

        return params;
    }

    public static HashMap<String,String> paramUpload(Context context, Bitmap bitmap){
        String base64Photo = EncodeBitmapUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

        Item_ChuaGiao itemChuaGiao = SharePreferenceUtil.getValueSaleItem(context);

        HashMap<String, String> params = new HashMap<>();
        params.put("ImageBase64String", base64Photo);
        params.put("SaleReceiptId", itemChuaGiao.getSaleReceiptId());
        params.put("LocalFileName", "");
        params.put("FileName", "");
        params.put("ContentType", "");
        params.put("Extention", "");

        return params;
    }

    public static HashMap<String, String> paramSentSMS(String NumberPhone, String Minutes){
        HashMap<String,String> params = new HashMap<>();
        params.put("Phone", NumberPhone);
        params.put("SoPhut", Minutes);

        return params;
    }

    public static String ApiSentSMS(){
        String DOMAIN_SENTSMS = "salereceipt/sendsmsdelivery";
        return URL2 + DOMAIN_SENTSMS;
    }
}
