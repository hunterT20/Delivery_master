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
import com.thanhtuan.delivery.sharePreference.MyShare;
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

                /*Bắt Json Object User*/
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LOGIN, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getBoolean("Result")){
                                        JSONObject data = response.getJSONObject("Data");

                                        User user = new User();
                                        user.setUserID(data.getInt("UserId"));
                                        user.setUserName(data.getString("UserName"));
                                        user.setLoginDate(data.getString("LoginDate"));
                                        user.setExpiredDate(data.getString("ExpiredDate"));

                                        /*Gắn biến share ID chuyền ID để làm PARAM cho API khác*/
                                        SharedPreferences pre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor edit = pre.edit();
                                        edit.putString(MyShare.VALUE_ID, data.getString("Id"));
                                        edit.apply();

                                        newtonCradleLoading.stop();
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                        //Save username và password
                                        if (ckbSaveUser.isChecked()){
                                            saveUser();
                                        }

                                        int STATUS = pre.getInt("status",0);
                                        if (STATUS != 0 && STATUS != 3) {
                                            Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
                                            startActivity(intent);
                                            finish();
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
        UsernameValue = edtUserName.getText().toString();
        PasswordValue = edtPassword.getText().toString();

        SharedPreferences MyPre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPre.edit();
        editor.putString(MyShare.VALUE_USERNAME, UsernameValue);
        editor.putString(MyShare.VALUE_PASSWORD, PasswordValue);
        editor.apply();
    }

    private void loadUser() {
        SharedPreferences MyPre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);

        String defaultUnameValue = "";
        UsernameValue = MyPre.getString(MyShare.VALUE_USERNAME, defaultUnameValue);
        String defaultPasswordValue = "";
        PasswordValue = MyPre.getString(MyShare.VALUE_PASSWORD, defaultPasswordValue);

        edtUserName.setText(UsernameValue);
        edtPassword.setText(PasswordValue);
    }
}
