package com.thanhtuan.delivery.data.remote;

import com.thanhtuan.delivery.data.model.ItemChuaGiao;
import com.thanhtuan.delivery.data.model.ItemDaGiao;
import com.thanhtuan.delivery.data.model.Product;
import com.thanhtuan.delivery.data.model.User;
import com.thanhtuan.delivery.data.model.VersionApp;
import com.thanhtuan.delivery.data.model.api.ApiListResult;
import com.thanhtuan.delivery.data.model.api.ApiResult;
import com.thanhtuan.delivery.data.model.map.Map;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    /*API Google Map*/
    @GET("directions/json?")
    Observable<Map> setupMap(
            @Query("Origin") String origin,
            @Query("destination") String address,
            @Query("language") String language,
            @Query("key") String key
    );

    /*API Delivery*/
    @POST("accounts/login")
    Observable<ApiResult<User>> login(@Body HashMap<String,String> paramLogin);

    @GET("salereceipt/version/ANDROID")
    Observable<ApiListResult<VersionApp>> checkVerson(@Header("Authorization") String token);

    @GET("salereceipt/employeeId")
    Observable<ApiListResult<ItemChuaGiao>> getItemChuaGiao(
            @Header("Authorization") String token,
            @Query("EmployeeId") String EmployeeId
    );

    @GET("salereceipt/deliveries/filter")
    Observable<ApiListResult<ItemDaGiao>> getItemDaGiao(
            @Header("Authorization") String token,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("salereceipt/detail/{ID}")
    Observable<ApiListResult<Product>> getListProduct(
            @Header("Authorization") String token
    );

    @POST("salereceipt/salereceiptabort")
    Observable<ApiResult<Integer>> huyGiaoHang(
            @Header("Authorization") String token,
            HashMap<String,String> param
    );

    @POST("salereceipt/sendsmsdelivery")
    Observable<ApiResult<String>> sentSMS(
            @Header("Authorization") String token,
            HashMap<String, String> param
    );

    @POST("salereceipt/salereceipttime")
    Observable<ApiResult<Integer>> timeRecord(
            @Header("Authorization") String token,
            HashMap<String,String> param
    );

    @POST("salereceipt/photoupload")
    Observable<ApiResult<String>> postPhoto(
            @Header("Authorization") String token,
            HashMap<String,String> param
    );

    @POST("salereceipt/salereceiptdone")
    Observable<ApiResult<String>> done(
            @Header("Authorization") String token,
            HashMap<String,String> param
    );
}
