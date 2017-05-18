package com.thanhtuan.delivery.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.btnLogin)    Button btnLogin;

    private static String api_login;

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
                api_login = ApiHelper.URL + ApiHelper.DOMAIN_LOGIN + "username=" + edtUserName.getText() + "&password=" + edtPassword.getText();
                Log.e("Login","pass: " + api_login);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, api_login, null,
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

                                        SharedPreferences pre=getSharedPreferences("MyPre", MODE_PRIVATE);
                                        SharedPreferences.Editor edit=pre.edit();
                                        edit.putString("ID", data.getString("Id"));
                                        edit.apply();

                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(LoginActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
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
