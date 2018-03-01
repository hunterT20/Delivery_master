package com.thanhtuan.delivery.data.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonRequest {

    public static void Request(Context context, final String Token, String URL, JSONObject jsonObject, Response.Listener<JSONObject> objectListener){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, jsonObject,
                objectListener, error -> Log.e("", "onErrorResponse: " + error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Authorization = new HashMap<>();
                Authorization.put("Authorization", Token);
                return Authorization;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }
}
