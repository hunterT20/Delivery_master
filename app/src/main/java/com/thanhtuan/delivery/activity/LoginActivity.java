package com.thanhtuan.delivery.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.rey.material.widget.CheckBox;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.model.User;
import com.victor.loading.newton.NewtonCradleLoading;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.btnLogin)    Button btnLogin;
    @BindView(R.id.newton_cradle_loading)  NewtonCradleLoading newtonCradleLoading;
    @BindView(R.id.ckbSave)     CheckBox ckbSaveUser;

    private static final String TAG = "LoginAcivity";
    private static String API_LOGIN;
    private static String PARAM1 = "username=";
    private static String PARAM2 = "&password=";
    private static final String PREFS_NAME = "MyPre";
    private static final String PREF_UNAME = "Username";
    private static final String PREF_PASSWORD = "Password";

    private String UsernameValue;
    private String PasswordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        addViews();
        addEvents();
    }

    private void addViews() {
        loadUser();
    }

    /*Khởi tạo Events*/
    private void addEvents() {
        /*Sự kiện click button Login*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Set view cho NewtonCradleLoading*/
                newtonCradleLoading.setVisibility(View.VISIBLE);
                newtonCradleLoading.start();
                newtonCradleLoading.setLoadingColor(Color.parseColor("#FFEB903C"));
                /*API_LOGIN*/
                API_LOGIN = ApiHelper.URL + ApiHelper.DOMAIN_LOGIN + PARAM1 + edtUserName.getText() + PARAM2 + edtPassword.getText();
                Log.e(TAG,"API: " + API_LOGIN);
                /*Bắt Json Object User*/
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LOGIN, null,
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

                                        /*Gắn biến share ID chuyền ID để xác nhận xác ID khác*/
                                        SharedPreferences pre = getSharedPreferences("MyPre", MODE_PRIVATE);
                                        SharedPreferences.Editor edit = pre.edit();
                                        edit.putString("ID", data.getString("Id"));
                                        edit.apply();

                                        newtonCradleLoading.stop();
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        if (ckbSaveUser.isChecked()){
                                            saveUser();
                                        }
                                        Log.e("ádsađasadsa",String.valueOf(pre.getInt("status",0)));
                                        if (pre.getInt("status",0) != 0) {
                                            if (pre.getInt("status", 0) != 3) {
                                                Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
                                                startActivity(intent);
                                            }else {
                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }else {
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }else {
                                        newtonCradleLoading.stop();
                                        newtonCradleLoading.setVisibility(View.GONE);
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

    private void saveUser() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        UsernameValue = edtUserName.getText().toString();
        PasswordValue = edtPassword.getText().toString();
        Log.e(TAG,"onPause save name: " + UsernameValue);
        Log.e(TAG,"onPause save password: " + PasswordValue);
        editor.putString(PREF_UNAME, UsernameValue);
        editor.putString(PREF_PASSWORD, PasswordValue);
        editor.apply();
    }

    private void loadUser() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Get value
        String defaultUnameValue = "";
        UsernameValue = settings.getString(PREF_UNAME, defaultUnameValue);
        String defaultPasswordValue = "";
        PasswordValue = settings.getString(PREF_PASSWORD, defaultPasswordValue);
        edtUserName.setText(UsernameValue);
        edtPassword.setText(PasswordValue);
        Log.e(TAG,"onResume load name: " + UsernameValue);
        Log.e(TAG,"onResume load password: " + PasswordValue);
    }
}
