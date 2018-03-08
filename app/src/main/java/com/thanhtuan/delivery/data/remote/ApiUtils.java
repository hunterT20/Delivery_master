package com.thanhtuan.delivery.data.remote;

import com.thanhtuan.delivery.BuildConfig;

public class ApiUtils {

    public ApiUtils() {
    }

    private static final String MAP_URL = BuildConfig.MAP_URL + "maps/api/";

    public static ApiService getAPIservices(){
        return RetrofitClient.getClient(BuildConfig.BASE_URL).create(ApiService.class);
    }

    public static ApiService getAPIMap(){
        return RetrofitClient.getMapApi(MAP_URL).create(ApiService.class);
    }
}
