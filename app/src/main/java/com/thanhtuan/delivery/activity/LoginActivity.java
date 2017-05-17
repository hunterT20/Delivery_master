package com.thanhtuan.delivery.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.btnLogin)    Button btnLogin;
    private static String url = "http://112.78.12.251:12358/api/Login?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        addEvents();
    }

    private void addEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = url + "username=" + edtUserName.getText() + "&password=" + edtPassword.getText();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getBoolean("Result")){
                                        User user = new User();
                                        JSONObject data = response.getJSONObject("Data");
                                        user.setUserID(data.getInt("UserId"));
                                        user.setUserName(data.getString("UserName"));
                                        user.setLoginDate(data.getString("LoginDate"));
                                        user.setExpiredDate(data.getString("ExpiredDate"));
                                        user.setID(data.getString("ID"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("", "onErrorResponse: " + error.getMessage());
                    }
                });

                VolleySingleton.getInstance(getApplication()).getRequestQueue().add(jsonObjectRequest);
            }
        });
    }
}
