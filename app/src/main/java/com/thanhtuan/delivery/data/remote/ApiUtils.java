package com.thanhtuan.delivery.data.remote;

import com.thanhtuan.delivery.BuildConfig;

public class ApiUtils {

    public ApiUtils() {
    }

    private static final String MAP_URL = BuildConfig.MAP_URL + "maps/api/" + BuildConfig.DOMAIN_MAP;

    public static ApiService getAPIservices(){
        return RetrofitClient.getClient(BuildConfig.BASE_URL).create(ApiService.class);
    }
}
