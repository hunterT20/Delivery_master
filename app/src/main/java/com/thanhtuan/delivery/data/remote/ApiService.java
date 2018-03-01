package com.thanhtuan.delivery.data.remote;

import com.thanhtuan.delivery.data.model.User;
import com.thanhtuan.delivery.data.model.api.ApiResult;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("accounts/login")
    Observable<ApiResult<User>> login(@Body HashMap<String,String> paramLogin);
}
