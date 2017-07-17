package com.thanhtuan.delivery.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.google.gson.Gson;
import com.thanhtuan.delivery.model.Item_ChuaGiao;

import static android.content.Context.MODE_PRIVATE;

public class SharePreferenceUtil {
    private static String NAME = "MyPre";

    private static String VALUE_ID = "ID";
    private static String VALUE_USERNAME = "Username";
    private static String VALUE_PASSWORD = "Password";
    private static String VALUE_SALEITEM = "SaleItem";
    private static String VALUE_STATUS = "status";
    private static String VALUE_DIRECTION = "Direction";
    private static String VALUE_DISTANCE = "Distance";
    private static String VALUE_TOKEN = "Token";

    public static void setValueId(Context context, String ID){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = MyPre.edit();
        edit.putString(VALUE_ID, ID);
        edit.apply();
    }

    public static String getValueId(Context context){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        return MyPre.getString(VALUE_ID, null);
    }

    public static void setValueToken(Context context, String Token){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = MyPre.edit();
        edit.putString(VALUE_TOKEN, Token);
        edit.apply();
    }

    public static String getValueToken(Context context){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        return MyPre.getString(VALUE_TOKEN, null);
    }

    public static void saveUser(Context context, String username, String pass) {
        SharedPreferences MyPre =  context.getSharedPreferences(NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPre.edit();
        editor.putString(VALUE_USERNAME, username);
        editor.putString(VALUE_PASSWORD, pass);
        editor.apply();
    }

    public static void loadUser(Context context, EditText edtUserName, EditText edtPassword) {
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        String UsernameValue = MyPre.getString(VALUE_USERNAME, "");
        String PasswordValue = MyPre.getString(VALUE_PASSWORD, "");

        edtUserName.setText(UsernameValue);
        edtPassword.setText(PasswordValue);
    }

    public static void setValueSaleitem(Context context, Item_ChuaGiao itemChuaGiao){
        Gson gson = new Gson();
        SharedPreferences MyPre = context.getSharedPreferences(NAME,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = MyPre.edit();

        String json = gson.toJson(itemChuaGiao);
        prefsEditor.putString(VALUE_SALEITEM, json);
        prefsEditor.apply();
    }

    public static Item_ChuaGiao getValueSaleItem(Context context){
        Gson gson = new Gson();
        SharedPreferences pre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        String json = pre.getString(VALUE_SALEITEM, "");
        return gson.fromJson(json, Item_ChuaGiao.class);
    }

    public static void setValueStatus(Context context, int status){
        SharedPreferences MyPre = context.getSharedPreferences(NAME,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = MyPre.edit();
        prefsEditor.putInt(VALUE_STATUS, status);
        prefsEditor.apply();
    }

    public static int getValueStatus(Context context){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        return MyPre.getInt(VALUE_STATUS, 0);
    }

    public static void setValueDirection(Context context, int Direction){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = MyPre.edit();
        edit.putInt(VALUE_DIRECTION, Direction);
        edit.apply();
    }

    public static int getValueDirection(Context context){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        return MyPre.getInt(VALUE_DIRECTION, -1);
    }

    public static void setValueDistance(Context context,String distance){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = MyPre.edit();
        edit.putString(VALUE_DISTANCE, distance);
        edit.apply();
    }

    public static String getValueDistance(Context context){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        return MyPre.getString(VALUE_DISTANCE, "");
    }

    public static void Clean(Context context){
        SharedPreferences MyPre = context.getSharedPreferences(NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = MyPre.edit();
        edit.remove("VALUE_ID");
        edit.remove("VALUE_SALEITEM");
        edit.remove("VALUE_STATUS");
        edit.remove("VALUE_DIRECTION");
        edit.apply();
    }
}
